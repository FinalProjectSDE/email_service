package introsde.mandrillemail.resources;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Path("/message")
public class MessageResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@POST
	@Path("/welcome/{id}")
	@Consumes({ MediaType.TEXT_XML })
	@Produces(MediaType.TEXT_PLAIN)
	public String sendRegistrationConfirmation(String email, @PathParam("id") Long id) throws SAXException, IOException, ParserConfigurationException {
		
		Element node =  DocumentBuilderFactory
			    .newInstance()
			    .newDocumentBuilder()
			    .parse(new ByteArrayInputStream(email.getBytes()))
			    .getDocumentElement();
		
		if(node.getNodeName().equals("email")) {
			return send_email("Welcome to Virtual LifeCoach! <br>"
					+ " Please click here to activate your account: <br>"
					+ "<a href='https://www.beeminder.com/apps/authorize?client_id=47d0tfgx1r31s6tsk3oonb3fyym127m&redirect_uri=http://localhost:6914/registration-activation/register&response_type=token'>"
					+ "https://www.beeminder.com/apps/authorize?client_id=47d0tfgx1r31s6tsk3oonb3fyym127m&redirect_uri=http://localhost:6914/registration-activation/register&response_type=token</a>"
					+ "<br> Use the following data to login:<br> USERNAME: "+node.getTextContent()+" <br> PASSWORD: "+id+" <br><br>The VirtualLifeCoach staff",
					"Welcome to Virtual LifeCoach!  The VirtualLifeCoach staff",
					"Welcome to Virtual LifeCoach!",
					node.getTextContent());
		} else {
			return "ERROR: wrong input format. Should be: <email>youremail@example.dom</email>";
		}
	}


	String send_email(String html_content, String text_content, String subject, String to) {
		String message = "error";
		URL obj;
		try {
			obj = new URL("https://mandrillapp.com/api/1.0/messages/send.json");

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json");

			String str = "{"
					+ "\"key\": \"PGTXetoQhCypZC-GdAWEww\","
					+ "\"message\": {"
					+ "\"html\": \"<p>"+html_content+"</p>\","
					+ "\"text\": \""+text_content+"\","
					+ "\"subject\": \""+subject+"\","
					+ "\"from_email\": \"no-reply@virtuallifecoach.com\","
					+ "\"to\": ["
					+ "{"
					+ "\"email\": \""+to+"\","
					+ "\"type\": \"to\""
					+ "}"
					+ "],"
					+ "\"important\": false,"
					+ "\"track_opens\": null,"
					+ "\"track_clicks\": null,"
					+ "\"auto_text\": null,"
					+ "\"auto_html\": null,"
					+ "\"inline_css\": null,"
					+ "\"url_strip_qs\": null,"
					+ "\"preserve_recipients\": null,"
					+ "\"view_content_link\": null,"
					+ "\"tracking_domain\": null,"
					+ "\"signing_domain\": null,"
					+ "\"return_path_domain\": null,"
					+ "\"merge\": false,"
					+ "\"merge_language\": \"mailchimp\","
					+ "\"tags\": ["
					+ "\"password-resets\""
					+ "],"
					+ "\"google_analytics_domains\": ["
					+ "\"example.com\""
					+ "],"
					+ "\"google_analytics_campaign\": \"message.from_email@example.com\","
					+ "\"metadata\": {" + "\"website\": \"www.example.com\""
					+ "}" + "}," + "\"async\": false" + "}";
			byte[] outputInBytes = str.getBytes("UTF-8");
			OutputStream os = con.getOutputStream();
			os.write(outputInBytes);
			os.close();

			con.connect();
			InputStream is = null;
			try {
				is = con.getInputStream();
			} catch (Exception e) {
				is = con.getErrorStream();
			}
			int responseCode = -1;
			try {
				responseCode = con.getResponseCode();
			} catch (IOException ex1) {
				// check if it's eof, if yes retrieve code again
				if (-1 != ex1.getMessage().indexOf("EOF")) {
					try {
						responseCode = con.getResponseCode();
					} catch (IOException ex2) {
						System.out.println(ex2.getMessage());
						// handle exception
					}
				} else {
					System.out.println(ex1.getMessage());
					// handle exception
				}
			}
			message = con.getResponseMessage();

			System.out.println("=> Result: " + message);
			System.out.println("=> HTTP Status: " + responseCode);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return message;
	}
}