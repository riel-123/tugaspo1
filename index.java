import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;

public class EstimasiHargaBensinGUI{

    // Harga bensin per liter
    final static double HARGA_PERTALITE = 10000;
    final static double HARGA_PERTAMAX = 13500;
    final static double HARGA_PERTAMAX_TURBO = 17000;
    final static double HARGA_SOLAR = 8000;
    final static double HARGA_BIO_SOLAR = 9000;

    // Daftar kota dan jarak antar kota (dalam kilometer)
    String[] kotaOptions = {"Jakarta", "Bandung", "Surabaya", "Yogyakarta", "Bali"};
    static double[][] jarakAntarKota = {
            {0, 150, 800, 560, 1200},
            {150, 0, 650, 400, 1050},
            {800, 650, 0, 330, 400},
            {560, 400, 330, 0, 550},
            {1200, 1050, 400, 550, 0}
    };

    // Rasio konsumsi bensin (km per liter) untuk motor dan mobil
    static DefaultListModel<String> motorList = new DefaultListModel<>();
    static DefaultListModel<String> mobilList = new DefaultListModel<>();
    static DefaultListModel<Double> motorRasioList = new DefaultListModel<>();
    static DefaultListModel<Double> mobilRasioList = new DefaultListModel<>();

    public static void main(String[] args) {
        // Default data kendaraan dan rasio
        initDefaultData();

        // Frame utama
        JFrame frame = new JFrame("Estimasi Harga Bensin");
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(230, 240, 255));

        // Judul
        JLabel labelTitle = new JLabel("Estimasi Harga Bensin", JLabel.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 24));
        labelTitle.setForeground(new Color(0, 102, 204));
        mainPanel.add(labelTitle, BorderLayout.NORTH);

        // Panel Input
        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        inputPanel.setBackground(new Color(230, 240, 255));

        JLabel labelKendaraan = new JLabel("Pilih kendaraan:");
        String[] kendaraanOptions = {"Motor", "Mobil"};
        JComboBox<String> comboKendaraan = new JComboBox<>(kendaraanOptions);

        JLabel labelTipe = new JLabel("Pilih tipe:");
        JComboBox<String> comboTipe = new JComboBox<>();

        JLabel labelBensin = new JLabel("Pilih jenis bensin:");
        String[] bensinOptions = {"Pertalite", "Pertamax", "Pertamax Turbo", "Solar", "Bio Solar"};
        JComboBox<String> comboBensin = new JComboBox<>(bensinOptions);

        JLabel labelKotaAsal = new JLabel("Pilih kota asal:");
        String[] kotaOptions = {"Jakarta", "Bandung", "Surabaya", "Yogyakarta", "Bali"};
        JComboBox<String> comboKotaAsal = new JComboBox<>(kotaOptions);

        JLabel labelKotaTujuan = new JLabel("Pilih kota tujuan:");
        JComboBox<String> comboKotaTujuan = new JComboBox<>(kotaOptions);

        JLabel labelJarakManual = new JLabel("Atau masukkan jarak manual (km):");
        JTextField textJarakManual = new JTextField();

        // Pilihan Jalur (Hanya untuk Mobil)
        JLabel labelJalur = new JLabel("Pilih jalur:");
        String[] jalurOptions = {"Jalur Biasa", "Jalur Tol"};
        JComboBox<String> comboJalur = new JComboBox<>(jalurOptions);
        comboJalur.setEnabled(false); // Defaultnya tidak aktif

        inputPanel.add(labelKendaraan);
        inputPanel.add(comboKendaraan);
        inputPanel.add(labelTipe);
        inputPanel.add(comboTipe);
        inputPanel.add(labelBensin);
        inputPanel.add(comboBensin);
        inputPanel.add(labelKotaAsal);
        inputPanel.add(comboKotaAsal);
        inputPanel.add(labelKotaTujuan);
        inputPanel.add(comboKotaTujuan);
        inputPanel.add(labelJarakManual);
        inputPanel.add(textJarakManual);
        inputPanel.add(labelJalur);
        inputPanel.add(comboJalur);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Panel Output
        JPanel outputPanel = new JPanel(new BorderLayout(40, 40));
        outputPanel.setBackground(new Color(230, 240, 255));

        JLabel labelHasil = new JLabel("HASIL ESTIMASI", JLabel.CENTER);
        labelHasil.setFont(new Font("Arial", Font.BOLD, 24));
        labelHasil.setForeground(new Color(0, 102, 204));

        JTextArea textOutput = new JTextArea(8, 20);
        textOutput.setEditable(false);
        textOutput.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Ukuran font lebih besar
        textOutput.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204)));

        // Tombol Hitung
        JButton buttonHitung = new JButton("Hitung");
        buttonHitung.setBackground(new Color(0, 153, 76));
        buttonHitung.setForeground(Color.WHITE);
        buttonHitung.setFont(new Font("Arial", Font.BOLD, 20));

        // Tombol Tambah Kendaraan
        JButton buttonTambahKendaraan = new JButton("Tambah Kendaraan");
        buttonTambahKendaraan.setBackground(new Color(255, 153, 0));
        buttonTambahKendaraan.setForeground(Color.WHITE);
        buttonTambahKendaraan.setFont(new Font("Arial", Font.BOLD, 20));

        // Menambahkan komponen ke outputPanel
        outputPanel.add(labelHasil, BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(textOutput), BorderLayout.CENTER);
        outputPanel.add(buttonHitung, BorderLayout.SOUTH);

        // Menambahkan tombol "Tambah Kendaraan"
        outputPanel.add(buttonTambahKendaraan, BorderLayout.NORTH);

        mainPanel.add(outputPanel, BorderLayout.SOUTH);

        // Event Listener untuk ComboKendaraan
        comboKendaraan.addActionListener(_ -> {
            comboTipe.removeAllItems();
            comboJalur.setEnabled(false); // Menyembunyikan comboJalur jika kendaraan bukan mobil
            if (comboKendaraan.getSelectedItem().equals("Motor")) {
                for (int i = 0; i < motorList.size(); i++) {
                    comboTipe.addItem(motorList.get(i));
                }
            } else {
                for (int i = 0; i < mobilList.size(); i++) {
                    comboTipe.addItem(mobilList.get(i));
                }
                comboJalur.setEnabled(true); // Menampilkan comboJalur jika kendaraan adalah mobil
            }
        });

        // Event Listener untuk Tombol Hitung
        buttonHitung.addActionListener(_ -> {
            try {
                String kendaraan = (String) comboKendaraan.getSelectedItem();
                String tipe = (String) comboTipe.getSelectedItem();
                String bensin = (String) comboBensin.getSelectedItem();
                String kotaAsal = (String) comboKotaAsal.getSelectedItem();
                String kotaTujuan = (String) comboKotaTujuan.getSelectedItem();
                String jarakManualText = textJarakManual.getText().trim();

                double jarak;
                boolean menggunakanKota = jarakManualText.isEmpty();
                if (!menggunakanKota) {
                    jarak = Double.parseDouble(jarakManualText);
                    if (jarak <= 0) {
                        textOutput.setText("Jarak manual harus lebih dari 0!");
                        JOptionPane.showMessageDialog(frame, "Jarak manual harus lebih dari 0!", "Peringatan", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    if (kotaAsal.equals(kotaTujuan)) {
                        textOutput.setText("Kota asal dan tujuan tidak boleh sama!");
                        JOptionPane.showMessageDialog(frame, "Kota asal dan tujuan tidak boleh sama!", "Peringatan", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int indexAsal = comboKotaAsal.getSelectedIndex();
                    int indexTujuan = comboKotaTujuan.getSelectedIndex();
                    jarak = jarakAntarKota[indexAsal][indexTujuan];
                    
                    // Jika kendaraan mobil dan memilih jalur tol
                    if (kendaraan.equals("Mobil") && comboJalur.getSelectedItem().equals("Jalur Tol")) {
                        // Misalnya, jarak melalui tol adalah 20% lebih pendek
                        jarak *= 0.8; // Mengurangi jarak sebanyak 20% untuk tol
                    }
                }

                double rasioBensin = getRasioKonsumsi(kendaraan, tipe);
                double bensinDibutuhkan = jarak / rasioBensin;

                double hargaBensin = getHargaBensin(bensin);
                double biaya = bensinDibutuhkan * hargaBensin;

                DecimalFormat formatRupiah = new DecimalFormat("###,###.##");
                String hasil = String.format(""" 
                    Kendaraan: %s (%s)
                    Jenis Bensin: %s
                    Kota Asal: %s
                    Kota Tujuan: %s
                    Jarak: %.2f km
                    Bensin Dibutuhkan: %.2f liter
                    Estimasi Biaya: Rp %s""",
                    kendaraan, tipe, bensin,
                    kotaAsal, kotaTujuan, jarak, bensinDibutuhkan, formatRupiah.format(biaya)
                );

                textOutput.setText(hasil);

                // Menampilkan notifikasi pop-up dengan hasil estimasi
                JOptionPane.showMessageDialog(frame, hasil, "Hasil Estimasi", JOptionPane.INFORMATION_MESSAGE);

            } catch (HeadlessException | NumberFormatException ex) {
                textOutput.setText("Terjadi kesalahan: " + ex.getMessage());
            }
        });

        // Event Listener untuk Tombol Tambah Kendaraan
        buttonTambahKendaraan.addActionListener(_ -> {
            // Pilih jenis kendaraan (Motor atau Mobil)
            String[] kendaraanOption = {"Motor", "Mobil"};
            String selectedKendaraan = (String) JOptionPane.showInputDialog(
                    frame, 
                    "Pilih jenis kendaraan:",
                    "Tambah Kendaraan Baru", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    kendaraanOptions, 
                    kendaraanOptions[0]
            );

            if (selectedKendaraan != null) {
                JTextField inputKendaraan = new JTextField(15);
                JTextField inputRasio = new JTextField(5);

                JPanel panelInputKendaraan = new JPanel();
                panelInputKendaraan.setLayout(new GridLayout(2, 2));
                panelInputKendaraan.add(new JLabel("Nama Kendaraan:"));
                panelInputKendaraan.add(inputKendaraan);
                panelInputKendaraan.add(new JLabel("Rasio Konsumsi Bensin (km/l):"));
                panelInputKendaraan.add(inputRasio);

                int option = JOptionPane.showConfirmDialog(frame, panelInputKendaraan,
                        "Tambah Kendaraan Baru - " + selectedKendaraan, JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String kendaraanBaru = inputKendaraan.getText().trim();
                    String rasioText = inputRasio.getText().trim();

                    if (!kendaraanBaru.isEmpty() && !rasioText.isEmpty()) {
                        double rasio = Double.parseDouble(rasioText);
                        if (kendaraanOption[0].equals(selectedKendaraan)) {
                            motorList.addElement(kendaraanBaru);
                            motorRasioList.addElement(rasio);
                        } else {
                            mobilList.addElement(kendaraanBaru);
                            mobilRasioList.addElement(rasio);
                        }
                        JOptionPane.showMessageDialog(frame, "Kendaraan baru berhasil ditambahkan!");
                    }
                }
            }
        });

        // Menampilkan Frame
        frame.setContentPane(mainPanel);
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void initDefaultData() {
        // Data kendaraan motor
        motorList.addElement("Honda Beat");
        motorRasioList.addElement(45.0);
        motorList.addElement("Honda Vario");
        motorRasioList.addElement(40.0);
        motorList.addElement("Honda Supra");
        motorRasioList.addElement(50.0);
        motorList.addElement("Yamaha NMAX");
        motorRasioList.addElement(35.0);
        motorList.addElement("Honda PCX");
        motorRasioList.addElement(38.0);
        motorList.addElement("Yamaha Aerox");
        motorRasioList.addElement(36.0);
        motorList.addElement("Vespa Sprint");
        motorRasioList.addElement(33.0);
        motorList.addElement("Kawasaki KLX");
        motorRasioList.addElement(31.0);
        motorList.addElement("Honda CBR");
        motorRasioList.addElement(42.0);
        motorList.addElement("Honda Sonic");
        motorRasioList.addElement(41.0);

        // Data kendaraan mobil
        mobilList.addElement("Honda Jazz");
        mobilRasioList.addElement(14.0);
        mobilList.addElement("Toyota Avanza");
        mobilRasioList.addElement(12.0);
        mobilList.addElement("Toyota Pajero");
        mobilRasioList.addElement(10.0);
        mobilList.addElement("Mitsubishi Expander");
        mobilRasioList.addElement(13.0);
        mobilList.addElement("Toyota Fortuner");
        mobilRasioList.addElement(11.0);
        mobilList.addElement("Honda CRV");
        mobilRasioList.addElement(11.0);
        mobilList.addElement("Honda Jeep");
        mobilRasioList.addElement(10.0);
        mobilList.addElement("Honda Civic");
        mobilRasioList.addElement(15.0);
        mobilList.addElement("Honda Brio");
        mobilRasioList.addElement(20.0);
        mobilList.addElement("Toyota Innova");
        mobilRasioList.addElement(11.0);
    }

    // Mengambil rasio konsumsi bensin kendaraan
    private static double getRasioKonsumsi(String kendaraan, String tipe) {
        if ("Motor".equals(kendaraan)) {
            for (int i = 0; i < motorList.size(); i++) {
                if (motorList.get(i).equals(tipe)) {
                    return motorRasioList.get(i);
                }
            }
        } else {
            for (int i = 0; i < mobilList.size(); i++) {
                if (mobilList.get(i).equals(tipe)) {
                    return mobilRasioList.get(i);
                }
            }
        }
        return 0;
    }

    // Mengambil harga bensin sesuai dengan jenis bensin
    private static double getHargaBensin(String bensin) {
        return switch (bensin) {
            case "Pertalite" -> HARGA_PERTALITE;
            case "Pertamax" -> HARGA_PERTAMAX;
            case "Pertamax Turbo" -> HARGA_PERTAMAX_TURBO;
            case "Solar" -> HARGA_SOLAR;
            case "Bio Solar" -> HARGA_BIO_SOLAR;
            default -> 0;
        };
    }
}
