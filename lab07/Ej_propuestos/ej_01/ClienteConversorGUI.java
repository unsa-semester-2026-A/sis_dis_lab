package Ej_propuestos.ej_01;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ClienteConversorGUI extends JFrame {
    private JTextField txtTemperatura;
    private JComboBox<String> comboConversion;
    private JLabel lblResultado;
    private JButton btnConvertir;
    private ConversorSOAP conversor;
    private JLabel lblStatus;

    public ClienteConversorGUI() {
        // Establecer Look and Feel nativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar y usar predeterminado
        }

        setTitle("Conversor de Temperatura SOAP");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel Principal con padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));
        setContentPane(mainPanel);

        // Encabezado
        JLabel lblTitulo = new JLabel("Conversor de Temperatura SOAP", JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(33, 37, 41));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        // Panel Central (Formulario)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 1: Temperatura
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblTemp = new JLabel("Temperatura:");
        lblTemp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblTemp, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        txtTemperatura = new JTextField();
        txtTemperatura.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtTemperatura, gbc);

        // Fila 2: Tipo de Conversion
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblTipo = new JLabel("Conversion:");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblTipo, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        String[] opciones = {"Celsius a Fahrenheit (°C -> °F)", "Fahrenheit a Celsius (°F -> °C)"};
        comboConversion = new JComboBox<>(opciones);
        comboConversion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(comboConversion, gbc);

        // Fila 3: Boton
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        btnConvertir = new JButton("Convertir Remotamente");
        btnConvertir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConvertir.setBackground(new Color(16, 185, 129));
        btnConvertir.setForeground(Color.WHITE);
        btnConvertir.setFocusPainted(false);
        formPanel.add(btnConvertir, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel Inferior (Resultado y Estado)
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(new Color(245, 247, 250));

        lblResultado = new JLabel("Resultado: Esperando entrada...", JLabel.CENTER);
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblResultado.setForeground(new Color(37, 99, 235));
        lblResultado.setBorder(new EmptyBorder(10, 0, 5, 0));
        bottomPanel.add(lblResultado, BorderLayout.NORTH);

        lblStatus = new JLabel("Iniciando conexion con SOAP...", JLabel.LEFT);
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(Color.GRAY);
        bottomPanel.add(lblStatus, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Accion del boton
        btnConvertir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarConversion();
            }
        });

        // Inicializar el cliente SOAP en un hilo separado
        conectarSOAP();
    }

    private void conectarSOAP() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    URL url = new URL("http://localhost:8081/conversor?wsdl");
                    QName qname = new QName("http://conversor.soap/", "ConversorSOAPService");
                    Service service = Service.create(url, qname);
                    conversor = service.getPort(ConversorSOAP.class);
                } catch (Exception ex) {
                    throw new RuntimeException("No se pudo conectar al servidor SOAP. Asegurese de que el Publicador este ejecutandose.");
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    lblStatus.setText("Conexion SOAP activa en http://localhost:8081/conversor");
                    lblStatus.setForeground(new Color(16, 185, 129));
                } catch (Exception e) {
                    lblStatus.setText("Error: " + e.getCause().getMessage());
                    lblStatus.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(ClienteConversorGUI.this, 
                        e.getCause().getMessage(), 
                        "Error de Conexion SOAP", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void realizarConversion() {
        if (conversor == null) {
            JOptionPane.showMessageDialog(this, 
                "El cliente SOAP no esta conectado. Reintentando conexion...", 
                "Error", 
                JOptionPane.WARNING_MESSAGE);
            conectarSOAP();
            return;
        }

        String inputStr = txtTemperatura.getText().trim();
        if (inputStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese un valor de temperatura.", 
                "Entrada Vacia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            final double tempInput = Double.parseDouble(inputStr);
            final int index = comboConversion.getSelectedIndex();
            
            btnConvertir.setEnabled(false);
            btnConvertir.setText("Procesando...");
            lblResultado.setText("Enviando peticion SOAP...");

            SwingWorker<Double, Void> worker = new SwingWorker<Double, Void>() {
                @Override
                protected Double doInBackground() throws Exception {
                    if (index == 0) {
                        return conversor.cToF(tempInput);
                    } else {
                        return conversor.fToC(tempInput);
                    }
                }

                @Override
                protected void done() {
                    btnConvertir.setEnabled(true);
                    btnConvertir.setText("Convertir Remotamente");
                    try {
                        double resultado = get();
                        if (index == 0) {
                            lblResultado.setText(String.format("Resultado: %.2f °F", resultado));
                        } else {
                            lblResultado.setText(String.format("Resultado: %.2f °C", resultado));
                        }
                    } catch (Exception e) {
                        lblResultado.setText("Error al realizar la conversion.");
                        JOptionPane.showMessageDialog(ClienteConversorGUI.this, 
                            "Error al invocar el servicio SOAP: " + e.getMessage(), 
                            "Error de Servicio", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingrese un numero decimal valido.", 
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClienteConversorGUI().setVisible(true);
            }
        });
    }
}
