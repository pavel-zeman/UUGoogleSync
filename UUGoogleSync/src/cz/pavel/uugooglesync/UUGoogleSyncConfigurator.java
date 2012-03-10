package cz.pavel.uugooglesync;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import cz.pavel.uugooglesync.utils.Configuration;
import cz.pavel.uugooglesync.utils.LogUtils;
import cz.pavel.uugooglesync.uu.UUManager;

public class UUGoogleSyncConfigurator extends JFrame implements ActionListener {

	private static final String GOOGLE_REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";
	private static final String GOOGLE_SCOPE = "https://www.googleapis.com/auth/calendar";
	
	private static final Logger log = LogUtils.getLogger();
	
	private static final long serialVersionUID = 1092198808287297052L;
	
	private GridBagLayout layout = new GridBagLayout();
	
	
	private JTextField syncInterval;
	private JTextField syncDaysBefore;
	private JTextField syncDaysAfter;
	private JTextField uuAccessCode1;
	private JTextField uuAccessCode2;
	private JButton okButton;
	private JTextArea googleUrl;
	private JTextField googleCode;
	private JTextField googleCalendarId;
	
	public UUGoogleSyncConfigurator() {
		this.setLayout(layout);
		this.setTitle("UUGoogleSync configurator");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		this.add(getLabel("Interval pro synchronizaci (minuty)"), getConstraints(0, 0));
		this.add(syncInterval = getTextField(10), getConstraints(1, 0, true, 2));

		this.add(getLabel("Počet dní pro synchronizaci před a po aktuálním dnu"), getConstraints(0, 1));
		this.add(syncDaysBefore = getTextField(5), getConstraints(1, 1, true));
		this.add(syncDaysAfter = getTextField(5), getConstraints(2, 1, true));

		
		this.add(getLabel("UU access code 1"), getConstraints(0, 2));
		this.add(uuAccessCode1 = getPasswordField(12), getConstraints(1, 2, true, 2));
		
		this.add(getLabel("UU access code 2"), getConstraints(0, 3));
		this.add(uuAccessCode2 = getPasswordField(12), getConstraints(1, 3, true, 2));

		this.add(getLabel(" "), getConstraints(0, 4, true, 3));
		this.add(getLabel("V prohlížeči přejděte na následující URL a postupujte podle instrukcí (stačí při prvním použití)"), getConstraints(0, 5, true, 3));
		GridBagConstraints googleUrlConstraints = getConstraints(0, 6, true, 3);
		googleUrlConstraints.fill = GridBagConstraints.BOTH;
		googleUrlConstraints.weighty = 1;
		this.add(googleUrl = getTextArea("", 10, 5), googleUrlConstraints);
		googleUrl.setEditable(false);
		googleUrl.setLineWrap(true);

		this.add(getLabel("Kód vygenerovaný pomocí předchozího URL"), getConstraints(0, 7));
		this.add(googleCode = getTextField(10), getConstraints(1, 7, true, 2));

		this.add(getLabel("ID kalendáře Google"), getConstraints(0, 8));
		this.add(googleCalendarId = getTextField(20), getConstraints(1, 8, true, 2));

		
		
		this.add(getLabel(" "), getConstraints(0, 9, true, 3));
		

		
		this.add(okButton = getButton("OK"), getConstraints(0, 10, true, 3));
		
		okButton.addActionListener(this);
		
		this.getRootPane().setDefaultButton(okButton);
	}
	
	private static JTextArea getTextArea(String text, int columns, int rows) {
		JTextArea textArea = new JTextArea(rows, columns);
		textArea.setBorder(new LineBorder(Color.LIGHT_GRAY));
		return textArea;
	}
	
	private static JButton getButton(String text) {
		return new JButton(text);
	}
	
	private static JLabel getLabel(String text) {
		return new JLabel(text);
	}
	
	private static JTextField getTextField(int characters) {
		JTextField textField = new JTextField(characters);
		return textField;
	}
	
	private static JTextField getPasswordField(int characters) {
		return new JPasswordField(characters);
	}
	
	private static GridBagConstraints getConstraints(int x, int y) {
		return getConstraints(x, y, 1);
	}

	private static GridBagConstraints getConstraints(int x, int y, int width) {
		return getConstraints(x, y, false);
	}

	private static GridBagConstraints getConstraints(int x, int y, boolean fill) {
		return getConstraints(x, y, fill, 1);
	}

	private static GridBagConstraints getConstraints(int x, int y, boolean fill, int width) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.insets = new Insets(2, 5, 2, 5);
		if (fill) {
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1;
		}
		constraints.gridwidth = width;
		constraints.anchor = GridBagConstraints.WEST;
		return constraints;
	}
	
	public void setData() throws IOException {
		Configuration.readProperties();
		syncInterval.setText(String.valueOf(Configuration.getInt(Configuration.Parameters.SYNC_INTERVAL)));
		syncDaysBefore.setText(String.valueOf(Configuration.getInt(Configuration.Parameters.SYNC_DAYS_BEFORE)));
		syncDaysAfter.setText(String.valueOf(Configuration.getInt(Configuration.Parameters.SYNC_DAYS_AFTER)));
		uuAccessCode1.setText(Configuration.getEncryptedString(Configuration.Parameters.UU_ACCESS_CODE1));
		uuAccessCode2.setText(Configuration.getEncryptedString(Configuration.Parameters.UU_ACCESS_CODE2));
		googleCalendarId.setText(Configuration.getString(Configuration.Parameters.GOOGLE_CALENDAR_ID));
		

		// generate URL for Google SSO
	    String clientId = Configuration.getEncryptedString(Configuration.Parameters.GOOGLE_CLIENT_ID);
	    
	    
	    googleUrl.setText(new GoogleAuthorizationRequestUrl(clientId, GOOGLE_REDIRECT_URL, GOOGLE_SCOPE).build());

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Configuration.setInt(Configuration.Parameters.SYNC_INTERVAL, Integer.parseInt(syncInterval.getText()));
			Configuration.setInt(Configuration.Parameters.SYNC_DAYS_BEFORE, Integer.parseInt(syncDaysBefore.getText()));
			Configuration.setInt(Configuration.Parameters.SYNC_DAYS_AFTER, Integer.parseInt(syncDaysAfter.getText()));
			Configuration.setString(Configuration.Parameters.GOOGLE_CALENDAR_ID, googleCalendarId.getText());
			
			// check access codes
			UUManager uuManager = new UUManager();
			try {
				uuManager.checkAccessCodes(uuAccessCode1.getText(), uuAccessCode2.getText());
			} catch (Exception e) {
				log.error("Error when checking access codes", e);
				JOptionPane.showMessageDialog(this, "Chyba při ověřování přihlašovacích údajů do Unicorn Universe (nesprávné přihlašovací údaje?)", "Chyba", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Configuration.setEncryptedString(Configuration.Parameters.UU_ACCESS_CODE1, uuAccessCode1.getText());
			Configuration.setEncryptedString(Configuration.Parameters.UU_ACCESS_CODE2, uuAccessCode2.getText());
			
			if (googleCode.getText().length() > 0) {
				// generate tokens
			    HttpTransport httpTransport = new NetHttpTransport();
			    JacksonFactory jsonFactory = new JacksonFactory();
			    String clientId = Configuration.getEncryptedString(Configuration.Parameters.GOOGLE_CLIENT_ID);
			    String clientSecret = Configuration.getEncryptedString(Configuration.Parameters.GOOGLE_CLIENT_SECRET);
	
				try {
					AccessTokenResponse response = new GoogleAuthorizationCodeGrant(httpTransport, jsonFactory, clientId, clientSecret, googleCode.getText(), GOOGLE_REDIRECT_URL).execute();
					Configuration.setEncryptedString(Configuration.Parameters.GOOGLE_ACCESS_TOKEN, response.accessToken);
					Configuration.setEncryptedString(Configuration.Parameters.GOOGLE_REFRESH_TOKEN, response.refreshToken);
				} catch (IOException e) {
					log.error("Error when generating google token", e);
					JOptionPane.showMessageDialog(this, "Chyba při generování Google tokenů", "Chyba", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			try {
				Configuration.storeProperties();
			} catch (IOException ioe) {
				log.error("Error writing properties to file", ioe);
				JOptionPane.showMessageDialog(this, "Chyba při aktualizaci konfigurace", "Chyba", JOptionPane.ERROR_MESSAGE);
				return;
			}
			WindowEvent windowClosing = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
			this.dispatchEvent(windowClosing);
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}


	
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// exception means, that the specified L&F cannot be set, so it is safe to ignore it
		}
		
		UUGoogleSyncConfigurator configurator = new UUGoogleSyncConfigurator();
		configurator.setData();
		configurator.setVisible(true);
		configurator.pack();
		configurator.setResizable(false);
	}


}
