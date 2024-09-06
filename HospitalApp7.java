import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class HospitalApp7 {
	
	//HashMaps to store user details
	private static HashMap<String, Patient> patientsMap = new HashMap<>();
	private static HashMap<String, Doctor> doctorsMap = new HashMap<>();
	
    public static void main(String[] args) {
		//Declare variables at a scope accessible to both places
		String name = ""; //Initialize with appropriate default values
		String dateOfBirth = "";
		String username = "";
		String password = "";
		
        JFrame frame = new JFrame("Login and Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridBagLayout());
		
        // Labels for username and password fields with matching font size
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        // Set the font size for labels to match the buttons
        Font labelFont = new Font(usernameLabel.getFont().getName(), Font.PLAIN, 12);
        usernameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        // Username and password fields with slightly larger dimensions
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
		
        // Buttons with slightly larger dimensions
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        // Set preferred sizes for buttons
        loginButton.setPreferredSize(new Dimension(100, 40));
        registerButton.setPreferredSize(new Dimension(150, 40)); // Wider button for "Register"
		
        // Create GridBagConstraints for layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.set(5, 5, 5, 5); // Padding between components
		
		// Add components to the panel with GridBagConstraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span two columns
        panel.add(loginButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(registerButton, gbc);
		
		// Handle the "Register" button click event
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new registration window with options for patients and doctors
                createRegistrationOptionsWindow();
            }
        });
		// Add the panel to the frame
        frame.add(panel);
        frame.setSize(400, 300); // Adjusted frame size
        frame.setVisible(true);
		
		// Handle the "Login" button click event
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Get the entered username and password
				String enteredUsername = usernameField.getText();
				char[] enteredPasswordChars = passwordField.getPassword();
				String enteredPassword = new String(enteredPasswordChars);
				// Hash the entered password for comparison
				String hashedEnteredPassword = hashData(enteredPassword);
				// Check if the username exists in the patient map
				if (patientsMap.containsKey(enteredUsername)) {
					// Retrieve the patient object
					Patient patient = patientsMap.get(enteredUsername);
					// Check if the entered password matches the stored hashed password
					if (hashedEnteredPassword.equals(patient.password)) {
						// Successful login for the patient
						JOptionPane.showMessageDialog(null, "Patient Login Successful!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
						// Create and display the medical page
						PatientMedicalRecord medicalRecord = new PatientMedicalRecord(patient.name);
						MedicalPage.createMedicalPage(enteredUsername, medicalRecord);
					} 
					else {
						JOptionPane.showMessageDialog(null, "Invalid Password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
					}
				} 
				else {
					// Handle login for doctors similarly if needed
					JOptionPane.showMessageDialog(null, "Username not found. Please register or try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
				}
				
				if(doctorsMap.containsKey(enteredUsername)) {
                    // Retrieve the doctor object
                    Doctor doctor = doctorsMap.get(enteredUsername);
                    // Check if the entered password matches the stored hashed password
                    if (hashedEnteredPassword.equals(doctor.password)) {
                        // Successful login for the doctor
                        JOptionPane.showMessageDialog(null, "Doctor Login Successful!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
						//displayMedicalDetailsFromFile();
						HospitalApp7 hospitalApp = new HospitalApp7();
						hospitalApp.displayMedicalDetailsFromFile();
                    } 
					else {
                        JOptionPane.showMessageDialog(null, "Invalid Password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } 
				else {
                    // Handle login failure
                    JOptionPane.showMessageDialog(null, "Username not found. Please register or try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
			}
		});
	}
	
	private void displayMedicalDetailsFromFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader("medical_details.txt"))) {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			JOptionPane.showMessageDialog(null, "Medical Details:\n" + content.toString(), "Medical Details", JOptionPane.INFORMATION_MESSAGE);
		} 
		catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error reading medical details file.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static void openDoctorPortal(String enteredUsername, PatientMedicalRecord patientMedicalRecord ) {
		JFrame doctorPortalFrame = new JFrame("Doctor Portal - " + enteredUsername);
		doctorPortalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel doctorPortalPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbcDoctor = new GridBagConstraints();
		gbcDoctor.fill = GridBagConstraints.HORIZONTAL;
		
		gbcDoctor.insets.set(5, 5, 5, 5); // Padding between components	
		// Label and field 
		JLabel nameLabelDoctor = new JLabel("Name:");
		JTextField nameFieldDoctor = new JTextField(20);
		nameFieldDoctor.setText(patientMedicalRecord.getName());
		
		JLabel ageLabelDoctor = new JLabel("Age:");
		JTextField ageFieldDoctor = new JTextField(20);
		ageFieldDoctor.setText(patientMedicalRecord.getAge());
		
		JLabel heightLabelDoctor = new JLabel("Height:");
		JTextField heightFieldDoctor = new JTextField(20);
		heightFieldDoctor.setText(patientMedicalRecord.getHeight());
		
		JLabel weightLabelDoctor = new JLabel("Weight:");
		JTextField weightFieldDoctor = new JTextField(20);
		weightFieldDoctor.setText(patientMedicalRecord.getWeight());
		
		JLabel bloodgroupLabelDoctor = new JLabel("Blood Group:");
		JTextField bloodgroupFieldDoctor = new JTextField(20);
		bloodgroupFieldDoctor.setText(patientMedicalRecord.getBloodgroup());
		
		JLabel bloodpressureLabelDoctor = new JLabel("Blood Pressure:");
		JTextField bloodpressureFieldDoctor = new JTextField(20);
		bloodpressureFieldDoctor.setText(patientMedicalRecord.getBloodpressure());
		
		JLabel diabetesLabelDoctor = new JLabel("Diabetes:");
		JTextField diabetesFieldDoctor = new JTextField(20);
		diabetesFieldDoctor.setText(patientMedicalRecord.hasDiabetes() ? "Yes" : "No");
		
		JLabel allergiesLabelDoctor = new JLabel("Allergies:");
		JTextField allergiesFieldDoctor = new JTextField(20);
		allergiesFieldDoctor.setText(patientMedicalRecord.getAllergies());
		
		JLabel medicationsLabelDoctor = new JLabel("Medications:");
		JTextField medicationsFieldDoctor = new JTextField(20);
		medicationsFieldDoctor.setText(patientMedicalRecord.getMedications());
		
		JLabel symptomsLabelDoctor = new JLabel("Symptoms:");
		JTextField symptomsFieldDoctor = new JTextField(20);
		symptomsFieldDoctor.setText(patientMedicalRecord.getSymptoms());
		
		// Add components to the doctor portal panel
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 0;
		doctorPortalPanel.add(nameLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 0;
		doctorPortalPanel.add(nameFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 1;
		doctorPortalPanel.add(ageLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 1;
		doctorPortalPanel.add(ageFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 2;
		doctorPortalPanel.add(heightLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 2;
		doctorPortalPanel.add(heightFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 3;
		doctorPortalPanel.add(weightLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 3;
		doctorPortalPanel.add(weightFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 4;
		doctorPortalPanel.add(bloodgroupLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 4;
		doctorPortalPanel.add(bloodgroupFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 5;
		doctorPortalPanel.add(bloodpressureLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 5;
		doctorPortalPanel.add(bloodpressureFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 6;
		doctorPortalPanel.add(diabetesLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 6;
		doctorPortalPanel.add(diabetesFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 7;
		doctorPortalPanel.add(allergiesLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 7;
		doctorPortalPanel.add(allergiesFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 8;
		doctorPortalPanel.add(medicationsLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 8;
		doctorPortalPanel.add(medicationsFieldDoctor, gbcDoctor);
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 9;
		doctorPortalPanel.add(symptomsLabelDoctor, gbcDoctor);
		gbcDoctor.gridx = 1;
		gbcDoctor.gridy = 9;
		doctorPortalPanel.add(symptomsFieldDoctor, gbcDoctor);
		
		// Add a button for logout
		JButton logoutButtonDoctor = new JButton("Logout");
		gbcDoctor.gridx = 0;
		gbcDoctor.gridy = 10;
		gbcDoctor.gridwidth = 2; // Span two columns
		doctorPortalPanel.add(logoutButtonDoctor, gbcDoctor);
		
		logoutButtonDoctor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close the Doctor Portal window
				doctorPortalFrame.dispose();
			}
		});
		
        doctorPortalFrame.add(doctorPortalPanel);
        doctorPortalFrame.setSize(400, 300);
        doctorPortalFrame.setVisible(true);
    }
	
    // Method to create a new registration window with options for patients and doctors
    private static void createRegistrationOptionsWindow() {
        JFrame registrationOptionsFrame = new JFrame("Registration Options");
        registrationOptionsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Create a panel for registration options
        JPanel registrationOptionsPanel = new JPanel(new GridBagLayout());
        // Buttons for registration options
        JButton patientButton = new JButton("Register as Patient");
        JButton doctorButton = new JButton("Register as Doctor");
        // Add registration options buttons to the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.set(5, 5, 5, 5); // Padding between components
        gbc.gridx = 0;
        gbc.gridy = 0;
        registrationOptionsPanel.add(patientButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        registrationOptionsPanel.add(doctorButton, gbc);
		
        // Handle the "Register as Patient" button click event
        patientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new patient registration window
                createPatientRegistrationWindow();
            }
        });
		
        // Handle the "Register as Doctor" button click event
        doctorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a new doctor registration window
                createDoctorRegistrationWindow();
            }
        });
		
        // Add the registration options panel to the registration options frame
        registrationOptionsFrame.add(registrationOptionsPanel);
        registrationOptionsFrame.setSize(300, 150); // Adjusted frame size
        registrationOptionsFrame.setVisible(true);
    }
	
    // Method to create a new registration window for patients
    private static void createPatientRegistrationWindow() {
        JFrame patientRegistrationFrame = new JFrame("Patient Registration");
        patientRegistrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
        // Create a panel for patient registration
        JPanel patientRegistrationPanel = new JPanel(new GridBagLayout());
        // Labels and text fields for name, date of birth, username, and password
        JLabel nameLabel = new JLabel("Name:");
        JLabel dobLabel = new JLabel("Date of Birth:");
        JLabel newUsernameLabel = new JLabel("Username:");
        JLabel newPasswordLabel = new JLabel("Password");
        JTextField nameField = new JTextField(15);
        JTextField dobField = new JTextField(15);
        JTextField newUsernameField = new JTextField(15);
        JPasswordField newPasswordField = new JPasswordField(15);
        // Submit button for patient registration
        JButton submitButton = new JButton("Submit");
		
        // Add components to the patient registration panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.set(5, 5, 5, 5); // Padding between components
        gbc.gridx = 0;
        gbc.gridy = 0;
        patientRegistrationPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        patientRegistrationPanel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        patientRegistrationPanel.add(dobLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        patientRegistrationPanel.add(dobField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        patientRegistrationPanel.add(newUsernameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        patientRegistrationPanel.add(newUsernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        patientRegistrationPanel.add(newPasswordLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        patientRegistrationPanel.add(newPasswordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span two columns
        patientRegistrationPanel.add(submitButton, gbc);
		
        // Handle the "Submit" button click event for patient registration
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the registration details entered by the patient
                String name = nameField.getText();
                String dateOfBirth = dobField.getText();
                String newUsername = newUsernameField.getText();
                char[] passwordChars = newPasswordField.getPassword();//get the password as char array
				// Hash the password before storing it
                String hashedName = hashData(name);
				String hashedDateOfBirth = hashData(dateOfBirth);
				String hashedUsername = hashData(newUsername);
                String hashedPassword = hashData(new String(passwordChars));
				//Create a Patient object with the registration details
				Patient patient = new Patient(/*String name, */name, hashedName, hashedDateOfBirth, hashedUsername, hashedPassword);
				// Store the patient details in the HashMap
                patientsMap.put(newUsername, patient);
				//Display patient details in the HashMap
				displayPatientDetails();
                // You can add your patient registration logic here
                // For this example, we'll display a simple success message using JOptionPane
                String message = "Patient Registration Successful!\nName: " + name + "\nDate of Birth: " + dateOfBirth + "\nUsername: " + newUsername;
                JOptionPane.showMessageDialog(null, message, "Patient Registration Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
		
        // Add the patient registration panel to the patient registration frame
        patientRegistrationFrame.add(patientRegistrationPanel);
        patientRegistrationFrame.setSize(300, 250); // Adjusted frame size
        patientRegistrationFrame.setVisible(true);
	}
	// New patientLogin method
	private static void patientLogin() {
		String enteredUsername = /* get username input */"z";
		char[] enteredPasswordChars = /* get password input as char array */"y".toCharArray();
		String enteredPassword = hashData(new String(enteredPasswordChars));
		// Check if the entered credentials match any registered patient
		Patient patient = patientsMap.get(enteredUsername);
		if (patient != null && patient.password.equals(enteredPassword)) {
			// Successful login
			System.out.println("Patient Login Successful!");
			// Proceed with further actions for logged-in patients
			// For example, you might open a patient dashboard or perform other tasks
		} 
		else {
			// Invalid login
			System.out.println("Invalid Patient Login!");
		}
	}
	// Method to create a new registration window for doctors
    private static void createDoctorRegistrationWindow() {
        JFrame doctorRegistrationFrame = new JFrame("Doctor Registration");
		doctorRegistrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Create a panel for doctor registration
        JPanel doctorRegistrationPanel = new JPanel(new GridBagLayout());
        // Labels and text fields for name, doctor ID, specialisation, username, and password
        JLabel nameLabel = new JLabel("Name:");
        JLabel doctorIdLabel = new JLabel("Doctor ID:");
        JLabel specialisationLabel = new JLabel("Specialisation:");
        JLabel newUsernameLabel = new JLabel("Username:");
        JLabel newPasswordLabel = new JLabel("Password");
        JTextField nameField = new JTextField(15);
        JTextField doctorIdField = new JTextField(15);
        JTextField specialisationField = new JTextField(15);
        JTextField newUsernameField = new JTextField(15);
        JPasswordField newPasswordField = new JPasswordField(15);
        // Submit button for doctor registration
        JButton submitButton = new JButton("Submit");
        // Add components to the doctor registration panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.set(5, 5, 5, 5); // Padding between components
        gbc.gridx = 0;
        gbc.gridy = 0;
        doctorRegistrationPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        doctorRegistrationPanel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        doctorRegistrationPanel.add(doctorIdLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        doctorRegistrationPanel.add(doctorIdField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        doctorRegistrationPanel.add(specialisationLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        doctorRegistrationPanel.add(specialisationField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        doctorRegistrationPanel.add(newUsernameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        doctorRegistrationPanel.add(newUsernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        doctorRegistrationPanel.add(newPasswordLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        doctorRegistrationPanel.add(newPasswordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span two columns
        doctorRegistrationPanel.add(submitButton, gbc);
        // Handle the "Submit" button click event for doctor registration
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the registration details entered by the doctor
                String name = nameField.getText();
                String doctorId = doctorIdField.getText();
                String specialisation = specialisationField.getText();
                String newUsername = newUsernameField.getText();
                char[] passwordChars = newPasswordField.getPassword();//get the password as a char array
				// Hash the password before storing it
                String hashedName = hashData(name);
				String hashedDoctorId = hashData(doctorId);
				String hashedSpecialisation = hashData(specialisation);
				String hashednewUsername = hashData(newUsername);
				String hashedPassword = hashData(new String(passwordChars));
				// Create a Doctor object with the registration details
                Doctor doctor = new Doctor(hashedName, hashedDoctorId, hashedSpecialisation, hashednewUsername, hashedPassword);
                // Store the doctor details in the HashMap
                doctorsMap.put(newUsername, new Doctor(hashedName, hashedDoctorId, hashedSpecialisation, hashednewUsername, hashedPassword));
				//Display doctor registtration details
				displayDoctorDetails();
                // You can add your doctor registration logic here
                // For this example, we'll display a simple success message using JOptionPane
                String message = "Doctor Registration Successful!\nName: " + name + "\nDoctor ID: " + doctorId + "\nSpecialisation: " + specialisation + "\nUsername: " + newUsername;
                JOptionPane.showMessageDialog(null, message, "Doctor Registration Success", JOptionPane.INFORMATION_MESSAGE);
            }
		});
		// Add the doctor registration panel to the doctor registration frame
		doctorRegistrationFrame.add(doctorRegistrationPanel);
		doctorRegistrationFrame.setSize(300, 250); // Adjusted frame size
		doctorRegistrationFrame.setVisible(true);
	}
	// Method to display patient registration details
	private static void displayPatientDetails() {
		System.out.println("Patient Registration Details:");
		try (FileWriter writer = new FileWriter("patient_details.txt", true)){
			for (Patient patient : patientsMap.values()) {
				String patientDetails = "Name: " + patient.name + ", Date of Birth: " + patient.dateOfBirth + ", Username: " + patient.username + ", Password: " + patient.password + "\n";
				System.out.println(patientDetails);
				writer.write(patientDetails);
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	// Method to display doctor registration details
	private static void displayDoctorDetails() {
		System.out.println("Doctor Registration Details:");
		try (FileWriter writer = new FileWriter("doctor_details.txt", true)){
			for (Doctor doctor : doctorsMap.values()) {
				String doctorDetails = "Name: " + doctor.name + ", Doctor ID: " + doctor.doctorId + ", Specialisation: " + doctor.specialisation + ", Username: " + doctor.username + ", Password: " + doctor.password + "\n";
				System.out.println(doctorDetails);
				writer.write(doctorDetails);
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	private static void createDoctorDashboard(String username) {
        JFrame doctorDashboardFrame = new JFrame("Doctor Dashboard - " + username);
        doctorDashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel doctorDashboardPanel = new JPanel();
        // Add components or customize the doctor's dashboard as needed
        doctorDashboardFrame.add(doctorDashboardPanel);
        doctorDashboardFrame.setSize(600, 400);
        doctorDashboardFrame.setVisible(true);
    }
	// New doctorLogin method
	private static void doctorLogin() {
		String enteredUsername = /* get username input */"z";
		char[] enteredPasswordChars = /* get password input as char array */"y".toCharArray();
		String enteredPassword = hashData(new String(enteredPasswordChars));
		// Check if the entered credentials match any registered doctor
		Doctor doctor = doctorsMap.get(enteredUsername);
		if (doctor != null && doctor.password.equals(enteredPassword)) {
			// Successful login
			System.out.println("Doctor Login Successful!");
			// Proceed with further actions for logged-in doctors
			// For example, you might open a doctor dashboard or perform other tasks
			 // Open the doctor's dashboard or landing page
            SwingUtilities.invokeLater(() -> createDoctorDashboard(enteredUsername));
        } 
		else {
			// Invalid login
			System.out.println("Invalid Doctor Login!");
		}
	}	
	private static void medicalDetailsToFile(PatientMedicalRecord medicalRecord) {
        //String fileName = "medical_details.txt";
        //String medicalDetails = "Name: " + medicalRecord.getName() + ", Age: " + medicalRecord.getAge() + ", Height: " + medicalRecord.getHeight() + ", Weight: " + medicalRecord.getWeight() + ", Blood Group:" + medicalRecord.getBloodgroup() + ", Blood Pressure:" + medicalRecord.getBloodpressure() + ", Diabetes:" + medicalRecord.hasDiabetes() + ", Allergies:" + medicalRecord.getAllergies() + ", Medications:" + medicalRecord.getMedications() + ", Symptoms:" + medicalRecord.getSymptoms() + ", Specialisation:" + medicalRecord.getSpecialisation() + "\n";
        try (FileWriter writer = new FileWriter("medical_details.txt", true)) {
			String medicalDetails = "Name: " + medicalRecord.getName() + ", Age: " + medicalRecord.getAge() + ", Height: " + medicalRecord.getHeight() + ", Weight: " + medicalRecord.getWeight() + ", Blood Group:" + medicalRecord.getBloodgroup() + ", Blood Pressure:" + medicalRecord.getBloodpressure() + ", Diabetes:" + medicalRecord.hasDiabetes() + ", Allergies:" + medicalRecord.getAllergies() + ", Medications:" + medicalRecord.getMedications() + ", Symptoms:" + medicalRecord.getSymptoms() /*+ ", Specialisation:" + medicalRecord.getSpecialisation() */+ "\n";

            writer.write(medicalDetails);
            //System.out.println("Medical details written to " + fileName);
        } 
		catch (IOException e) {
            e.printStackTrace();
        }
    }
	// Method to hash a password using SHA-256
	private static String hashData(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data.getBytes());
			byte[] byteData = md.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte b : byteData) {
               hexString.append(String.format("%02x", b));
			}
			return hexString.toString();
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	// Class representing a Patient
    static class Patient {
        private String name;
		private String hashedName;
        private String dateOfBirth;
        private String username;
        private String password;
        public Patient(String name, String hashedName, String dateOfBirth, String username, String password) {
            this.name = name;
			this.hashedName = hashedName;
            this.dateOfBirth = dateOfBirth;
            this.username = username;
            this.password = password;
        }
    }
    // Class representing a Doctor
    static class Doctor {
        private String name;
        private String doctorId;
        private String specialisation;
        private String username;
        private String password;
        public Doctor(String name, String doctorId, String specialisation, String username, String password) {
            this.name = name;
            this.doctorId = doctorId;
            this.specialisation = specialisation;
            this.username = username;
            this.password = password;
        }
    }
	static class PatientMedicalRecord {
		private String name;
		private String hashedName;
		private String age;
		private String height;
		private String weight;
		private String bloodgroup;
		private String bloodpressure;
		private boolean diabetes;
		private String allergies;
		private String medications;
		private String symptoms;
		private String specialisation;
		public PatientMedicalRecord(String name) {
			this.name = name;
			this.hashedName = hashedName;
			this.age = age;
			this.height = height;
			this.weight = weight;
			this.bloodgroup = bloodgroup;
			this.bloodpressure = bloodpressure;
			this.diabetes = diabetes;
			this.allergies = allergies;
			this.medications = medications;
			this.symptoms = symptoms;
			this.specialisation = specialisation;
		}
		//getters
		public String getName() {
			return name;
		}
		public String gethashedName() {
			return hashedName;
		}
		public String getAge() {
			return age;
		}
		public String getHeight() {
			return height;
		}
		public String getWeight() {
			return weight;
		}
		public String getBloodgroup() {
			return bloodgroup;
		}
		public String getBloodpressure() {
			return bloodpressure == null ? "" : bloodpressure;
		}
		public boolean hasDiabetes() {
			return diabetes;
		}
		public String getAllergies() {
			return allergies;
		}
		public String getMedications() {
			return medications;
		}
		public String getSymptoms() {
			return symptoms;
		}
		public String getSpecialisation() {
			return specialisation;
		}
		// Setters
		public void setName(String name) {
			this.name = name;
		}
		public void sethashedName(String hashedName) {
			this.hashedName = hashedName;
		}
		public void setAge(String age) {
			this.age = age;
		}
		public void setHeight(String height) {
			this.height = height;
		}
		public void setWeight(String weight) {
			this.weight = weight;
		}
		public void setBloodgroup(String bloodgroup) {
			this.bloodgroup = bloodgroup;
		}
		public void setBloodpressure(String bloodpressure) {
			this.bloodpressure = bloodpressure;
		}
		public void setDiabetes(boolean diabetes) {
			this.diabetes = diabetes;	
		}
		public void setAllergies(String allergies) {
			this.allergies = allergies;
		}
		public void setMedications(String medications) {
			this.medications = medications;
		}
		public void setSymptoms(String symptoms) {
			this.symptoms = symptoms;
		}
		public void setSpecialisation(String specialisation) {
			this.specialisation = specialisation;
		}
		// Add a method to display the medical record details
		public String displayMedicalRecord() {
			// Customize the format based on your needs
			return "Name: " + name + "\nAge: " + age + "\nHeight: " + height + "\nWeight: " + weight + "\nBlood Group: " + bloodgroup + "\nBlood Pressure: " + bloodpressure + "\nDiabetes: " + (diabetes ? "Yes" : "No") + "\nAllergies: " + allergies + "\nMedications: " + medications + "\nSymptoms: " + symptoms + "\nSpecialisation: " + specialisation;
		}
	}
	static class MedicalPage {
		public static void createMedicalPage(String username, PatientMedicalRecord medicalRecord) {
			JFrame medicalPageFrame = new JFrame("Medical Page - " + username);
			medicalPageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			String decryptedName = decryptData(medicalRecord.getName());
			JPanel medicalPagePanel = new JPanel(new GridBagLayout());
			// Add labels and text fields for medical information
			// Add components to the medical page panel
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets.set(5, 5, 5, 5); // Padding between components
			// Label and field for Name
			JLabel nameLabel = new JLabel("Name:");
			JTextField nameField = new JTextField(20);
			//nameField.setText(/*medicalRecord.getName()*/decryptedName); // Set the initial value if available
			nameField.setText(medicalRecord.getName() != null ? medicalRecord.getName() : medicalRecord.gethashedName());
			JLabel ageLabel = new JLabel("Age:");
			JTextField ageField = new JTextField(20);
			ageField.setText(medicalRecord.getAge()); // Set the initial value if available
			JLabel heightLabel = new JLabel("Height:");
			JTextField heightField = new JTextField(20);
			heightField.setText(medicalRecord.getHeight());
			JLabel weightLabel = new JLabel("Weight:");
			JTextField weightField = new JTextField(20);
			weightField.setText(medicalRecord.getWeight()); // Set the initial value if available
			JLabel bloodgroupLabel = new JLabel("Blood Group:");
			JTextField bloodgroupField = new JTextField(20);
			bloodgroupField.setText(medicalRecord.getBloodgroup());
			JLabel bloodpressureLabel = new JLabel("Blood Pressure:");
			JTextField bloodpressureField = new JTextField(20);
			bloodpressureField.setText(medicalRecord.getBloodpressure()); // Set the initial value if available
			JLabel diabetesLabel = new JLabel("Diabetes:");
			JTextField diabetesField = new JTextField(20);
			diabetesField.setText(Boolean.toString(medicalRecord.hasDiabetes()));
			JLabel allergiesLabel = new JLabel("Allergies:");
			JTextField allergiesField = new JTextField(20);
			allergiesField.setText(medicalRecord.getAllergies()); // Set the initial value if available
			JLabel medicationsLabel = new JLabel("Medications:");
			JTextField medicationsField = new JTextField(20);
			medicationsField.setText(medicalRecord.getMedications());
			// Label and field for Symptoms
			JLabel symptomsLabel = new JLabel("Symptoms:");
			JTextField symptomsField = new JTextField(20);
			symptomsField.setText(medicalRecord.getSymptoms()); // Set the initial value if available
			// Label and field for Specialisation
			JLabel specialisationLabel = new JLabel("Specialisation:");
			JTextField specialisationField = new JTextField(20);
			specialisationField.setText(medicalRecord.getSpecialisation()); // Set the initial value if available
			gbc.gridx = 0;
			gbc.gridy = 0;
			medicalPagePanel.add(nameLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 0;
			medicalPagePanel.add(nameField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 1;
			medicalPagePanel.add(ageLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 1;
			medicalPagePanel.add(ageField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 2;
			medicalPagePanel.add(heightLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 2;
			medicalPagePanel.add(heightField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 3;
			medicalPagePanel.add(weightLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 3;
			medicalPagePanel.add(weightField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 4;
			medicalPagePanel.add(bloodgroupLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 4;
			medicalPagePanel.add(bloodgroupField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 5;
			medicalPagePanel.add(bloodpressureLabel, gbc);
			JRadioButton highRadioButton = new JRadioButton("High");
			JRadioButton lowRadioButton = new JRadioButton("Normal");
			JRadioButton normalRadioButton = new JRadioButton("Low");
			ButtonGroup bloodpressureButtonGroup = new ButtonGroup();
			bloodpressureButtonGroup.add(highRadioButton);
			bloodpressureButtonGroup.add(normalRadioButton);
			bloodpressureButtonGroup.add(lowRadioButton);
			// Set the selected radio button based on the medical record data
			switch (medicalRecord.getBloodpressure()) {
				case "High":
					highRadioButton.setSelected(true);
					break;
				case "Normal":
					lowRadioButton.setSelected(true);
					break;
				case "Low":
					normalRadioButton.setSelected(true);
					break;
			}
			gbc.gridx = 1;
			gbc.gridy = 5;
			medicalPagePanel.add(highRadioButton, gbc);
			gbc.gridx = 2;
			gbc.gridy = 5;
			medicalPagePanel.add(normalRadioButton, gbc);
			gbc.gridx = 3;
			gbc.gridy = 5;
			medicalPagePanel.add(lowRadioButton, gbc);
			// Checkbox for diabetes
			JCheckBox diabetesCheckbox = new JCheckBox("Diabetes");
			diabetesCheckbox.setSelected(medicalRecord.hasDiabetes());
			gbc.gridx = 0;
			gbc.gridy = 6;
			medicalPagePanel.add(diabetesLabel, gbc);
			JRadioButton yesRadioButton = new JRadioButton("Yes");
			JRadioButton noRadioButton = new JRadioButton("No");
			ButtonGroup diabetesButtonGroup = new ButtonGroup();
			diabetesButtonGroup.add(yesRadioButton);
			diabetesButtonGroup.add(noRadioButton);
			if (medicalRecord.hasDiabetes()) {
				yesRadioButton.setSelected(true);
			} 
			else {
				noRadioButton.setSelected(true);
			}
			gbc.gridx = 1;
			gbc.gridy = 6;
			medicalPagePanel.add(yesRadioButton, gbc);
			gbc.gridx = 2;
			gbc.gridy = 6;
			medicalPagePanel.add(noRadioButton, gbc);
			gbc.gridx = 0;
			gbc.gridy = 7;
			medicalPagePanel.add(allergiesLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 7;
			medicalPagePanel.add(allergiesField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 8;
			medicalPagePanel.add(medicationsLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 8;
			medicalPagePanel.add(medicationsField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 9;
			medicalPagePanel.add(symptomsLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 9;
			medicalPagePanel.add(symptomsField, gbc);
			gbc.gridx = 0;
			gbc.gridy = 10;
			medicalPagePanel.add(specialisationLabel, gbc);
			gbc.gridx = 1;
			gbc.gridy = 10;
			medicalPagePanel.add(specialisationField, gbc);
			JButton submitButton = new JButton("Submit");
			JButton appointmentButton = new JButton("Make Appointment");
			gbc.gridx = 0;
			gbc.gridy = 11;
			gbc.gridwidth = 2; // Span two columns
			medicalPagePanel.add(submitButton, gbc);
			gbc.gridx = 0;
			gbc.gridy = 12;
			gbc.gridwidth = 2; // Span two columns
			medicalPagePanel.add(appointmentButton, gbc);
			
			submitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// You can update the medical record object and perform any necessary actions
					medicalRecord.setName(nameField.getText());
					medicalRecord.setAge(ageField.getText());
					medicalRecord.setHeight(heightField.getText());
					medicalRecord.setWeight(weightField.getText());
					medicalRecord.setBloodgroup(bloodgroupField.getText());
					medicalRecord.setBloodpressure(bloodpressureField.getText());
					medicalRecord.setDiabetes(diabetesCheckbox.isSelected());
					medicalRecord.setAllergies(allergiesField.getText());
					medicalRecord.setMedications(medicationsField.getText());
					medicalRecord.setSymptoms(symptomsField.getText());
					medicalRecord.setSpecialisation(specialisationField.getText());
					// Display updated medical record
					JOptionPane.showMessageDialog(null, medicalRecord.displayMedicalRecord(), "Medical Information Updated", JOptionPane.INFORMATION_MESSAGE);
					medicalDetailsToFile(medicalRecord);
				}
			});
			appointmentButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Handle the appointment button click
					// You can implement appointment-related logic here
					JOptionPane.showMessageDialog(null, "Appointment Button Clicked", "Appointment", JOptionPane.INFORMATION_MESSAGE);
					showAppointmentDialog(medicalRecord);
				}
			});
			medicalPageFrame.add(medicalPagePanel);
			medicalPageFrame.setSize(400, 300);
			medicalPageFrame.setVisible(true);
		}
		private static String decryptData(String encryptedData) {	
			return encryptedData;
		}
		// Method to store patient medical details in a file
		private static void storePatientMedicalDetails(PatientMedicalRecord medicalRecord) {
			try (FileWriter writer = new FileWriter("patient_medical_details.txt", true)) {
				String medicalDetails = "Name: " + medicalRecord.getName() + ", Age: " + medicalRecord.getAge() + ", Height: " + medicalRecord.getHeight() + ", Weight: " + medicalRecord.getWeight() + ", Blood Group:" + medicalRecord.getBloodgroup() + ", Blood Pressure:" + medicalRecord.getBloodpressure() + ", Diabetes:" + medicalRecord.hasDiabetes() + ", Allergies:" + medicalRecord.getAllergies() + ", Medications:" + medicalRecord.getMedications() + ", Symptoms:" + medicalRecord.getSymptoms() /*+ ", Specialisation:" + medicalRecord.getSpecialisation() */+ "\n";
				writer.write(medicalDetails);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Method to store doctor medical details in a file
		private static void storeDoctorMedicalDetails(Doctor doctor) {
			try (FileWriter writer = new FileWriter("doctor_medical_details.txt", true)) {
				String medicalDetails = "Name: " + doctor.name + ", Doctor ID: " + doctor.doctorId + ", Specialisation: " + doctor.specialisation +"\n";
				writer.write(medicalDetails);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Method to show the appointment dialog
        private static void showAppointmentDialog(PatientMedicalRecord medicalRecord) {            
            JButton submitButton = new JButton("Submit Appointment");
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "Appointment Button Clicked", "Appointment", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
    }
}