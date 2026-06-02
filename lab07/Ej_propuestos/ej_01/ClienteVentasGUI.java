package Ej_propuestos.ej_01;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class ClienteVentasGUI extends JFrame {

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdProducto;
    private JTextField txtCantidad;
    private JButton btnComprar;
    private JButton btnActualizar;
    private VentasSOAP servicioVentas;
    private JLabel lblStatus;

    public ClienteVentasGUI() {
        // Establecer Look and Feel nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar
        }

        setTitle("Sistema de Ventas en Linea SOAP");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 244, 248));
        setContentPane(mainPanel);

        // Cabecera
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 244, 248));
        JLabel lblTitulo = new JLabel(
            "Catalogo de Productos - Cliente SOAP",
            JLabel.LEFT
        );
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(30, 41, 59));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        btnActualizar = new JButton("Actualizar Catalogo");
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualizar.setBackground(new Color(59, 130, 246));
        btnActualizar.setForeground(Color.BLACK);
        btnActualizar.setFocusPainted(false);
        headerPanel.add(btnActualizar, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabla de Productos en ScrollPane
        String[] columnas = {
            "ID Producto",
            "Nombre del Producto",
            "Precio Unitario ($)",
            "Stock Disponible",
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductos.setRowHeight(24);
        tablaProductos
            .getTableHeader()
            .setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de Compra (Formulario)
        JPanel compraPanel = new JPanel(new GridBagLayout());
        compraPanel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Formulario de Compra",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(30, 41, 59)
            )
        );
        compraPanel.setBackground(new Color(240, 244, 248));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: ID Producto
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        JLabel lblId = new JLabel("ID Producto seleccionado:");
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        compraPanel.add(lblId, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        txtIdProducto = new JTextField();
        txtIdProducto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        compraPanel.add(txtIdProducto, gbc);

        // Fila 2: Cantidad
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        JLabel lblCantidad = new JLabel("Cantidad a comprar:");
        lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        compraPanel.add(lblCantidad, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        txtCantidad = new JTextField();
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        compraPanel.add(txtCantidad, gbc);

        // Boton Comprar
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        btnComprar = new JButton("Realizar Compra SOAP");
        btnComprar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComprar.setBackground(new Color(16, 185, 129));
        btnComprar.setForeground(Color.BLACK);
        btnComprar.setFocusPainted(false);
        compraPanel.add(btnComprar, gbc);

        mainPanel.add(compraPanel, BorderLayout.SOUTH);

        // Barra de Estado
        lblStatus = new JLabel(
            "Estableciendo conexion con servicio SOAP...",
            JLabel.LEFT
        );
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(Color.GRAY);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
        statusPanel.setBackground(new Color(240, 244, 248));
        statusPanel.add(lblStatus, BorderLayout.CENTER);

        // Vamos a reestructurar el layout del main panel para meter la barra de estado abajo
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        containerPanel.add(statusPanel, BorderLayout.SOUTH);
        setContentPane(containerPanel);

        // Escuchador para seleccionar productos haciendo click en la tabla
        tablaProductos.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int filaSeleccionada = tablaProductos.getSelectedRow();
                    if (filaSeleccionada != -1) {
                        Object idValue = modeloTabla.getValueAt(
                            filaSeleccionada,
                            0
                        );
                        txtIdProducto.setText(idValue.toString());
                        txtCantidad.requestFocus();
                    }
                }
            }
        );

        // Eventos
        btnActualizar.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cargarProductos();
                }
            }
        );

        btnComprar.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ejecutarCompra();
                }
            }
        );

        // Conectar al servicio
        conectarSOAP();
    }

    private void conectarSOAP() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    URL url = new URL("http://localhost:8082/ventas?wsdl");
                    QName qname = new QName(
                        "http://ventas.soap/",
                        "VentasSOAPService"
                    );
                    Service service = Service.create(url, qname);
                    servicioVentas = service.getPort(VentasSOAP.class);
                } catch (Exception ex) {
                    throw new RuntimeException(
                        "No se pudo conectar al servidor de Ventas. Verifique que PublicadorVentas este corriendo."
                    );
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    lblStatus.setText(
                        "Conexion SOAP de Ventas establecida en http://localhost:8082/ventas"
                    );
                    lblStatus.setForeground(new Color(16, 185, 129));
                    cargarProductos();
                } catch (Exception e) {
                    lblStatus.setText("Error: " + e.getCause().getMessage());
                    lblStatus.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(
                        ClienteVentasGUI.this,
                        e.getCause().getMessage(),
                        "Error de Conexion Ventas SOAP",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void cargarProductos() {
        if (servicioVentas == null) {
            return;
        }

        btnActualizar.setEnabled(false);
        lblStatus.setText("Cargando catalogo desde el servicio SOAP...");

        SwingWorker<Producto[], Void> worker = new SwingWorker<
            Producto[],
            Void
        >() {
            @Override
            protected Producto[] doInBackground() throws Exception {
                return servicioVentas.obtenerProductos();
            }

            @Override
            protected void done() {
                btnActualizar.setEnabled(true);
                try {
                    Producto[] productos = get();
                    modeloTabla.setRowCount(0);
                    for (Producto p : productos) {
                        modeloTabla.addRow(new Object[] {
                            p.getId(),
                            p.getNombre(),
                            p.getPrecio(),
                            p.getStock(),
                        });
                    }
                    lblStatus.setText(
                        "Catalogo actualizado. " +
                            productos.length +
                            " productos cargados."
                    );
                    lblStatus.setForeground(new Color(16, 185, 129));
                } catch (Exception e) {
                    lblStatus.setText("Error al cargar catalogo.");
                    lblStatus.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(
                        ClienteVentasGUI.this,
                        "Error al listar productos: " + e.getMessage(),
                        "Error SOAP",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    private void ejecutarCompra() {
        if (servicioVentas == null) {
            JOptionPane.showMessageDialog(
                this,
                "El servicio SOAP no esta conectado.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String idStr = txtIdProducto.getText().trim();
        String cantStr = txtCantidad.getText().trim();

        if (idStr.isEmpty() || cantStr.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Ingrese ID de Producto y Cantidad a comprar.",
                "Entrada Invalida",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            final int idProducto = Integer.parseInt(idStr);
            final int cantidad = Integer.parseInt(cantStr);

            btnComprar.setEnabled(false);
            btnComprar.setText("Procesando Venta...");

            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return servicioVentas.realizarVenta(idProducto, cantidad);
                }

                @Override
                protected void done() {
                    btnComprar.setEnabled(true);
                    btnComprar.setText("Realizar Compra SOAP");
                    try {
                        String resultado = get();
                        JOptionPane.showMessageDialog(
                            ClienteVentasGUI.this,
                            resultado,
                            "Resultado de la Operacion",
                            resultado.startsWith("Error")
                                ? JOptionPane.ERROR_MESSAGE
                                : JOptionPane.INFORMATION_MESSAGE
                        );

                        // Recargar catalogo para actualizar existencias
                        cargarProductos();
                        txtCantidad.setText("");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                            ClienteVentasGUI.this,
                            "Fallo al contactar servicio de venta: " +
                                e.getMessage(),
                            "Error de Conexion",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };
            worker.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Los campos de ID y Cantidad deben ser enteros validos.",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    new ClienteVentasGUI().setVisible(true);
                }
            }
        );
    }
}
