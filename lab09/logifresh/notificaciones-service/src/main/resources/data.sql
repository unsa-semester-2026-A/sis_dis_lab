INSERT INTO notificaciones (id, destinatario, asunto, mensaje, estado, fecha_envio) VALUES
(1,  'cliente1@email.com', 'Pedido confirmado #1',       'Su pedido #1 ha sido procesado exitosamente. Total: $3000.00',            'ENVIADA',  NOW()),
(2,  'cliente2@email.com', 'Pedido confirmado #2',       'Su pedido #2 ha sido procesado exitosamente. Total: $3800.00',            'ENVIADA',  NOW()),
(3,  'cliente3@email.com', 'Pedido confirmado #3',       'Su pedido #3 ha sido procesado exitosamente. Total: $5400.00',            'ENVIADA',  NOW()),
(4,  'cliente4@email.com', 'Pedido confirmado #4',       'Su pedido #4 ha sido procesado exitosamente. Total: $3000.00',            'ENVIADA',  NOW()),
(5,  'cliente5@email.com', 'Pedido confirmado #5',       'Su pedido #5 ha sido procesado exitosamente. Total: $2850.00',            'ENVIADA',  NOW()),
(6,  'cliente6@email.com', 'Pedido cancelado #6',        'Su pedido #6 ha sido cancelado.',                                          'ENVIADA',  NOW()),
(7,  'cliente7@email.com', 'Pedido confirmado #7',       'Su pedido #7 ha sido procesado exitosamente. Total: $4500.00',            'ENVIADA',  NOW()),
(8,  'cliente8@email.com', 'Pedido confirmado #8',       'Su pedido #8 ha sido procesado exitosamente. Total: $2200.00',            'ENVIADA',  NOW()),
(9,  'cliente9@email.com', 'Notificacion fallida #9',    'Intento de notificacion fallido para el pedido #9.',                      'ERROR',    NOW()),
(10, 'cliente10@email.com', 'Pedido confirmado #10',     'Su pedido #10 ha sido procesado exitosamente. Total: $1350.00',           'ENVIADA',  NOW())
ON CONFLICT (id) DO NOTHING;

ALTER TABLE notificaciones ALTER COLUMN id RESTART WITH 11;
