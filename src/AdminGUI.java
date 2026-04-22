import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd, btnEdit, btnDelete, btnView;

    public AdminGUI() {
        setTitle("Event Management - Admin");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Table for events
        model = new DefaultTableModel(
            new String[]{"Event ID", "Name", "Date", "Venue", "Description"}, 0
        );
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Buttons panel
        JPanel panel = new JPanel();
        btnAdd = new JButton("Add Event");
        btnEdit = new JButton("Edit Event");
        btnDelete = new JButton("Delete Event");
        btnView = new JButton("View Participants");

        panel.add(btnAdd);
        panel.add(btnEdit);
        panel.add(btnDelete);
        panel.add(btnView);
        add(panel, BorderLayout.SOUTH);

        // Load events
        loadEvents();

        // Button actions
        btnAdd.addActionListener(e -> addEvent());
        btnEdit.addActionListener(e -> editEvent());
        btnDelete.addActionListener(e -> deleteEvent());
        btnView.addActionListener(e -> viewParticipants());

        setVisible(true);
    }

    private void loadEvents() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM events";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getDate("event_date"),
                        rs.getString("venue"),
                        rs.getString("description")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading events");
        }
    }

    private void addEvent() {
        JTextField nameField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField venueField = new JTextField();
        JTextField descField = new JTextField();

        Object[] message = {
                "Name:", nameField,
                "Date:", dateField,
                "Venue:", venueField,
                "Description:", descField
        };

        int option = JOptionPane.showConfirmDialog(
            this, message, "Add Event", JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                String sql =
                    "INSERT INTO events(event_name,event_date,venue,description) VALUES(?,?,?,?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setString(2, dateField.getText());
                pst.setString(3, venueField.getText());
                pst.setString(4, descField.getText());
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Event added successfully!");
                loadEvents();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding event");
            }
        }
    }

    private void editEvent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to edit!");
            return;
        }

        int eventId = (int) table.getValueAt(selectedRow, 0);
        String oldName = (String) table.getValueAt(selectedRow, 1);
        String oldDate = table.getValueAt(selectedRow, 2).toString();
        String oldVenue = (String) table.getValueAt(selectedRow, 3);
        String oldDesc = (String) table.getValueAt(selectedRow, 4);

        JTextField nameField = new JTextField(oldName);
        JTextField dateField = new JTextField(oldDate);
        JTextField venueField = new JTextField(oldVenue);
        JTextField descField = new JTextField(oldDesc);

        Object[] message = {
                "Name:", nameField,
                "Date:", dateField,
                "Venue:", venueField,
                "Description:", descField
        };

        int option = JOptionPane.showConfirmDialog(
            this, message, "Edit Event", JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                String sql =
                    "UPDATE events SET event_name=?,event_date=?,venue=?,description=? WHERE event_id=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setString(2, dateField.getText());
                pst.setString(3, venueField.getText());
                pst.setString(4, descField.getText());
                pst.setInt(5, eventId);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Event updated successfully!");
                loadEvents();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating event");
            }
        }
    }

    private void deleteEvent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to delete!");
            return;
        }

        int eventId = (int) table.getValueAt(selectedRow, 0);

        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this event?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "DELETE FROM events WHERE event_id=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, eventId);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Event deleted successfully!");
                loadEvents();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting event");
            }
        }
    }

    
    private void viewParticipants() {
        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an event first!");
            return;
        }

        int eventId = (int) table.getValueAt(row, 0);

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                "SELECT p.participant_id, p.name, p.phone, p.email " +
                "FROM participants p " +
                "JOIN registration r ON p.participant_id = r.participant_id " +
                "WHERE r.event_id=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel pm = new DefaultTableModel(
                new String[]{"ID","Name","Phone","Email"}, 0
            );
            JTable pt = new JTable(pm);

            while (rs.next()) {
                pm.addRow(new Object[]{
                        rs.getInt("participant_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }

            JOptionPane.showMessageDialog(
                this,
                new JScrollPane(pt),
                "Registered Participants",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading participants");
        }
    }

    public static void main(String[] args) {
        new AdminGUI();
    }
}