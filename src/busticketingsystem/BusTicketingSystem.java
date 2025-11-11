package busticketingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.print.*;

public class BusTicketingSystem extends JFrame {
    private JTextField txtName, txtBusNumber, txtDestination, txtPrice, txtDiscount, txtTotal;
    private JComboBox<String> cmbType;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd, btnUpdate, btnDelete, btnReset;

    public BusTicketingSystem() {
        setTitle("Bus Ticketing System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.setBorder(BorderFactory.createTitledBorder("Ticket Information"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField();
        txtBusNumber = new JTextField();
        txtDestination = new JTextField();
        txtPrice = new JTextField();
        txtDiscount = new JTextField(); txtDiscount.setEditable(false); txtDiscount.setBackground(new Color(230,230,230));
        txtTotal = new JTextField(); txtTotal.setEditable(false); txtTotal.setBackground(new Color(230,230,230));

        cmbType = new JComboBox<>(new String[]{"Regular","Student","Senior"});

        gbc.gridx=0; gbc.gridy=0; formPanel.add(new JLabel("Passenger Name:"), gbc);
        gbc.gridx=1; gbc.gridy=0; formPanel.add(txtName, gbc);
        gbc.gridx=0; gbc.gridy=1; formPanel.add(new JLabel("Passenger Type:"), gbc);
        gbc.gridx=1; gbc.gridy=1; formPanel.add(cmbType, gbc);
        gbc.gridx=0; gbc.gridy=2; formPanel.add(new JLabel("Bus Number:"), gbc);
        gbc.gridx=1; gbc.gridy=2; formPanel.add(txtBusNumber, gbc);
        gbc.gridx=0; gbc.gridy=3; formPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx=1; gbc.gridy=3; formPanel.add(txtDestination, gbc);
        gbc.gridx=0; gbc.gridy=4; formPanel.add(new JLabel("Base Price:"), gbc);
        gbc.gridx=1; gbc.gridy=4; formPanel.add(txtPrice, gbc);
        gbc.gridx=0; gbc.gridy=5; formPanel.add(new JLabel("Discount:"), gbc);
        gbc.gridx=1; gbc.gridy=5; formPanel.add(txtDiscount, gbc);
        gbc.gridx=0; gbc.gridy=6; formPanel.add(new JLabel("Total Price:"), gbc);
        gbc.gridx=1; gbc.gridy=6; formPanel.add(txtTotal, gbc);

        btnAdd = new JButton("Add Ticket");
        btnUpdate = new JButton("Update Ticket");
        btnDelete = new JButton("Drop off");
        btnReset = new JButton("Reset");

        JPanel buttonPanel = new JPanel(new GridLayout(2,2,10,10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnAdd); buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete); buttonPanel.add(btnReset);

        gbc.gridx=0; gbc.gridy=7; gbc.gridwidth=2; formPanel.add(buttonPanel, gbc);
        add(formPanel, BorderLayout.WEST);

        // --- Table ---
        model = new DefaultTableModel(new String[]{
            "ID","Name","Type","Bus No.","Destination","Price","Discount","Total","Date/Time"
        },0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Legends ---
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,15,5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.add(new JLabel("Legend:"));
        legendPanel.add(new JLabel("Student = 10% discount"));
        legendPanel.add(new JLabel("Senior = 20% discount"));
        legendPanel.add(new JLabel("Regular = no discount"));
        add(legendPanel, BorderLayout.NORTH);

        // --- Listeners ---
        btnAdd.addActionListener(e -> addTicket());
        btnUpdate.addActionListener(e -> updateTicket());
        btnDelete.addActionListener(e -> deleteTicket());
        btnReset.addActionListener(e -> resetForm());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                int row = table.getSelectedRow();
                if(row!=-1){
                    txtName.setText(model.getValueAt(row,1).toString());
                    cmbType.setSelectedItem(model.getValueAt(row,2).toString());
                    txtBusNumber.setText(model.getValueAt(row,3).toString());
                    txtDestination.setText(model.getValueAt(row,4).toString());
                    txtPrice.setText(model.getValueAt(row,5).toString());
                    txtDiscount.setText(model.getValueAt(row,6).toString());
                    txtTotal.setText(model.getValueAt(row,7).toString());
                    btnAdd.setEnabled(false);
                }
            }
        });

        cmbType.addActionListener(e -> calculateDiscount());
        txtPrice.addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e){ calculateDiscount(); }
        });

        loadTickets();
    }

    // --- Discount calculation ---
    private void calculateDiscount(){
        try{
            double price = Double.parseDouble(txtPrice.getText().trim());
            String type = cmbType.getSelectedItem().toString();
            double discount = 0;
            if(type.equals("Student")) discount = price*0.10;
            else if(type.equals("Senior")) discount = price*0.20;
            double total = price - discount;
            txtDiscount.setText(String.format("%.2f",discount));
            txtTotal.setText(String.format("%.2f",total));
        }catch(Exception ignored){}
    }

    // --- Add ticket ---
    private void addTicket(){
        try{
            String name=txtName.getText().trim();
            String type=cmbType.getSelectedItem().toString();
            String busNum=txtBusNumber.getText().trim();
            String dest=txtDestination.getText().trim();
            double price=Double.parseDouble(txtPrice.getText().trim());
            double discount=Double.parseDouble(txtDiscount.getText().trim());
            double total=Double.parseDouble(txtTotal.getText().trim());

            try(Connection conn=DBConnection.connect()){
                String sql="INSERT INTO tickets (passenger_name,passenger_type,bus_number,destination,price,discount,total) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement pst=conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1,name); pst.setString(2,type); pst.setString(3,busNum);
                pst.setString(4,dest); pst.setDouble(5,price); pst.setDouble(6,discount);
                pst.setDouble(7,total); pst.executeUpdate();

                ResultSet rs=pst.getGeneratedKeys(); int ticketId=0;
                if(rs.next()) ticketId=rs.getInt(1);

                JOptionPane.showMessageDialog(this,"✅ Ticket Added Successfully!");
                resetForm(); loadTickets();
                printTicket(ticketId,name,type,busNum,dest,price,discount,total);
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this,"❌ Error adding ticket: "+ex.getMessage());
        }
    }

    // --- Print ticket using PrinterJob ---
    private void printTicket(int ticketId, String name, String type, String busNum, String dest, double price, double discount, double total) {
    JFrame ticketFrame = new JFrame("Bus Ticket - ID: " + ticketId);
    ticketFrame.setSize(400, 400);
    ticketFrame.setLocationRelativeTo(null);
    ticketFrame.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setBackground(Color.WHITE);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Title
    JLabel lblTitle = new JLabel("Bus Ticket", SwingConstants.CENTER);
    lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
    lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(lblTitle);
    panel.add(Box.createVerticalStrut(20));

    // Ticket details
    String[] details = {
        "Ticket ID: " + ticketId,
        "Passenger Name: " + name,
        "Passenger Type: " + type,
        "Bus Number: " + busNum,
        "Destination: " + dest,
        String.format("Price: PHP %.2f", price),
        String.format("Discount: PHP %.2f", discount),
        String.format("Total: PHP %.2f", total)
    };

    for (String detail : details) {
        JLabel lbl = new JLabel(detail, SwingConstants.CENTER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
    }

    panel.add(Box.createVerticalStrut(20));

    // Thank you message
    JLabel lblThanks = new JLabel("Thank you for riding with us!", SwingConstants.CENTER);
    lblThanks.setFont(new Font("Arial", Font.ITALIC, 14));
    lblThanks.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(lblThanks);

    // Print button
    JButton btnPrint = new JButton("Print Ticket");
    btnPrint.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnPrint.addActionListener(e -> {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Bus Ticket ID " + ticketId);
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            graphics.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
            panel.paint(graphics);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(ticketFrame, "Ticket printed successfully!");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(ticketFrame, "Error printing ticket: " + ex.getMessage());
            }
        }
    });

    panel.add(Box.createVerticalStrut(10));
    panel.add(btnPrint);

    ticketFrame.add(panel, BorderLayout.CENTER);
    ticketFrame.setVisible(true);
}


    // --- Update ticket ---
    private void updateTicket(){
        int row=table.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"Please select a ticket to update."); return;}
        try{
            int id=(int) model.getValueAt(row,0);
            String name=txtName.getText().trim();
            String type=cmbType.getSelectedItem().toString();
            String busNum=txtBusNumber.getText().trim();
            String dest=txtDestination.getText().trim();
            double price=Double.parseDouble(txtPrice.getText().trim());
            double discount=Double.parseDouble(txtDiscount.getText().trim());
            double total=Double.parseDouble(txtTotal.getText().trim());

            try(Connection conn=DBConnection.connect()){
                String sql="UPDATE tickets SET passenger_name=?,passenger_type=?,bus_number=?,destination=?,price=?,discount=?,total=? WHERE ticket_id=?";
                PreparedStatement pst=conn.prepareStatement(sql);
                pst.setString(1,name); pst.setString(2,type); pst.setString(3,busNum);
                pst.setString(4,dest); pst.setDouble(5,price); pst.setDouble(6,discount);
                pst.setDouble(7,total); pst.setInt(8,id); pst.executeUpdate();
                JOptionPane.showMessageDialog(this,"✅ Ticket Updated Successfully!");
                resetForm(); loadTickets();
            }
        }catch(Exception ex){ JOptionPane.showMessageDialog(this,"❌ Error updating ticket: "+ex.getMessage());}
    }

    // --- Delete ticket ---
    private void deleteTicket(){
        int row=table.getSelectedRow();
        if(row==-1){ JOptionPane.showMessageDialog(this,"⚠ Please select a passenger to drop off."); return;}
        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete this ticket?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;
        int id=(int) model.getValueAt(row,0);
        try(Connection conn=DBConnection.connect()){
            String sql="DELETE FROM tickets WHERE ticket_id=?";
            PreparedStatement pst=conn.prepareStatement(sql);
            pst.setInt(1,id); pst.executeUpdate();
            JOptionPane.showMessageDialog(this,"🗑 Ticket Deleted Successfully!");
            resetForm(); loadTickets();
        }catch(Exception ex){ JOptionPane.showMessageDialog(this,"❌ Error deleting ticket: "+ex.getMessage());}
    }

    // --- Load tickets ---
    private void loadTickets(){
        model.setRowCount(0);
        try(Connection conn=DBConnection.connect()){
            String sql="SELECT * FROM tickets ORDER BY date_time DESC";
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery(sql);
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("ticket_id"),
                    rs.getString("passenger_name"),
                    rs.getString("passenger_type"),
                    rs.getString("bus_number"),
                    rs.getString("destination"),
                    rs.getDouble("price"),
                    rs.getDouble("discount"),
                    rs.getDouble("total"),
                    rs.getTimestamp("date_time")
                });
            }
        }catch(Exception ex){ JOptionPane.showMessageDialog(this,"❌ Error loading data: "+ex.getMessage());}
    }

    // --- Reset form ---
    private void resetForm(){
        txtName.setText(""); txtBusNumber.setText(""); txtDestination.setText("");
        txtPrice.setText(""); txtDiscount.setText(""); txtTotal.setText("");
        cmbType.setSelectedIndex(0); table.clearSelection(); btnAdd.setEnabled(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new BusTicketingSystem().setVisible(true));
    }
}
