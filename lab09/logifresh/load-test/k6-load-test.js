import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

export const options = {
  vus: 100,
  duration: '5m',
  thresholds: {
    http_req_duration: ['p(95)<10000'],
    http_req_failed: ['rate<0.15'],
    pedido_duration: ['p(95)<10000'],
    error_rate: ['rate<0.15'],
  },
};

const errorRate = new Rate('error_rate');
const pedidoDuration = new Trend('pedido_duration');
const successCounter = new Counter('success_count');
const timeoutCounter = new Counter('timeout_count');
const error500Counter = new Counter('error_500_count');
const eofCounter = new Counter('eof_count');
const otrosCounter = new Counter('otros_count');

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081';

const CLIENTES = [
  'Supermercado Central', 'Mayorista SA', 'Cliente Ejemplo',
  'Distribuidora Norte', 'Alimentos del Sur', 'Comercial Pacifico',
  'Mercado Libre SA', 'Importadora Global', 'Productos Naturales EIRL',
  'Grupo Alimenticio Los Andes', 'Supermercados Unido', 'Frigorifico Austral',
  'Carnes Premium', 'Distribuidora de Alimentos SA',
  'Comercializadora del Centro', 'Embutidos del Valle',
  'Pescados Mar del Plata', 'Avicola San Juan', 'Lacteos del Oeste',
  'Congelados Express',
];

export function setup() {
  console.log('=== LogiFresh - Prueba de Carga ===');
  console.log('Endpoint: ' + BASE_URL + '/api/pedidos/procesar');
  console.log('Ramp-up: 20->50->100->50->0 en 6 minutos');
  console.log('===================================');
}

export default function () {
  const productoId = randomInt(1, 20);
  const cantidad = randomItem([5, 10, 15, 20, 25, 30, 50]);
  const precioUnitario = randomFloat(10.0, 500.0);

  const payload = JSON.stringify({
    cliente: randomItem(CLIENTES),
    productoId: productoId,
    cantidad: cantidad,
    precioUnitario: precioUnitario,
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
    timeout: '30s',
    tags: { endpoint: 'procesar', producto: 'prod-' + productoId },
  };

  const res = http.post(BASE_URL + '/api/pedidos/procesar', payload, params);

  if (res.status !== 0 && res.status !== null) {
    pedidoDuration.add(res.timings.duration);
  }

  const esExitoso = res.status === 200 || res.status === 201;

  const validation = check(res, {
    'status es 200 o 201': function () { return esExitoso; },
    'tiempo de respuesta < 30s': function () { return res.timings.duration < 30000; },
    'respuesta tiene body JSON': function () {
      try { JSON.parse(res.body); return true; }
      catch (e) { return false; }
    },
  });

  errorRate.add(validation ? 0 : 1);

  if (esExitoso) {
    successCounter.add(1);
  } else if (res.status === 0 || res.status === null) {
    if (res.error && res.error.includes('EOF')) {
      eofCounter.add(1);
    } else {
      timeoutCounter.add(1);
    }
  } else if (res.status >= 500) {
    error500Counter.add(1);
  } else {
    otrosCounter.add(1);
  }

  sleep(randomFloat(0.5, 1.5));
}

function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomFloat(min, max) {
  return parseFloat((Math.random() * (max - min) + min).toFixed(2));
}

function randomItem(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

export function handleSummary(data) {
  const d = data.metrics;

  return {
    'stdout': JSON.stringify({
      metadata: {
        testName: 'LogiFresh - Carga de pedidos (con rampa)',
        date: new Date().toISOString(),
        baseUrl: BASE_URL,
        stages: '20->50->100->50->0 en 6m',
        endpoint: '/api/pedidos/procesar',
      },
      summary: {
        http_reqs: {
          total: d.http_reqs ? d.http_reqs.values.count : 0,
          rate: d.http_reqs ? d.http_reqs.values.rate + '/s' : 0,
        },
        http_req_duration: {
          avg_ms: d.http_req_duration ? d.http_req_duration.values.avg : 0,
          min_ms: d.http_req_duration ? d.http_req_duration.values.min : 0,
          med_ms: d.http_req_duration ? d.http_req_duration.values.med : 0,
          max_ms: d.http_req_duration ? d.http_req_duration.values.max : 0,
          p90_ms: d.http_req_duration ? d.http_req_duration.values["p(90)"] : 0,
          p95_ms: d.http_req_duration ? d.http_req_duration.values["p(95)"] : 0,
        },
        http_req_failed: {
          rate: d.http_req_failed ? d.http_req_failed.values.rate : 0,
          passes: d.http_req_failed ? d.http_req_failed.values.passes : 0,
          fails: d.http_req_failed ? d.http_req_failed.values.fails : 0,
        },
        counters: {
          success: d.success_count ? d.success_count.values.count : 0,
          eof: d.eof_count ? d.eof_count.values.count : 0,
          timeout: d.timeout_count ? d.timeout_count.values.count : 0,
          error_500: d.error_500_count ? d.error_500_count.values.count : 0,
          otros: d.otros_count ? d.otros_count.values.count : 0,
        },
        pedido_duration: {
          avg_ms: d.pedido_duration ? d.pedido_duration.values.avg : 0,
          min_ms: d.pedido_duration ? d.pedido_duration.values.min : 0,
          med_ms: d.pedido_duration ? d.pedido_duration.values.med : 0,
          max_ms: d.pedido_duration ? d.pedido_duration.values.max : 0,
          p90_ms: d.pedido_duration ? d.pedido_duration.values["p(90)"] : 0,
          p95_ms: d.pedido_duration ? d.pedido_duration.values["p(95)"] : 0,
        },
        error_rate: {
          rate: d.error_rate ? d.error_rate.values.rate : 0,
        },
      },
      thresholds: {
        http_req_duration_p95: d.http_req_duration ? d.http_req_duration.values["p(95)"] < 15000 : false,
        http_req_failed: d.http_req_failed ? d.http_req_failed.values.rate < 0.30 : false,
        pedido_duration_p95: d.pedido_duration ? d.pedido_duration.values["p(95)"] < 15000 : false,
        error_rate: d.error_rate ? d.error_rate.values.rate < 0.30 : false,
      },
    }, null, 2),
  };
}
