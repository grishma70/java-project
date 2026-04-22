import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ParticipantGUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private int participantId;

    public ParticipantGUI(int participantId) {
        this.participantId = participantId;

        setTitle("Participant - Event Management");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Date", "Venue", "Description"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton registerBtn = new JButton("Register for Selected Event");
        add(registerBtn, BorderLayout.SOUTH);

        loadEvents();

        registerBtn.addActionListener(e -> registerForEvent());

        setVisible(true);
    }

    private void loadEvents() {
        try {
            Connection con = DBConnection.getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM events");
            model.setRowCount(0);
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("event_id"),
                        rs.getString("event_name"),
                        rs.getDate("event_date"),
                        rs.getString("venue"),
                        rs.getString("description")
                });
            }
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading events");
        }
    }

    private void registerForEvent() {
        int row = table.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Select an event first!");
            return;
        }

        int eventId = (int) table.getValueAt(row, 0);
        int participantId=this.participantId;
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM registration WHERE event_id=? AND participant_id=?");
            check.setInt(1, eventId);
            check.setInt(2, participantId);
            ResultSet rs = check.executeQuery();
            if(rs.next()) {
                JOptionPane.showMessageDialog(this, "Already registered!");
                return;
            }

            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO registration(event_id, participant_id) VALUES(?, ?)");
            pst.setInt(1, eventId);
            pst.setInt(2, participantId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");

        } catch(Exception e) {
    e.printStackTrace(); // prints exact SQL/Java error in console
    JOptionPane.showMessageDialog(this, "Error registering for event: " + e.getMessage());
    }
    }

    public static void main(String[] args) {
        // For testing participant ID = 2
        new ParticipantGUI(2);
    }
}