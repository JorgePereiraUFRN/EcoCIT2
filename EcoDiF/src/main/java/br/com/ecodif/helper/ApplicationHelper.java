package br.com.ecodif.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import br.com.ecodif.domain.Application;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.service.ApplicationService;

/**
 * Classe responsável pelas regras de negócio das aplicações.
 * 
 * @author Andreza Lima
 *
 */
public class ApplicationHelper extends OwnerHelper {

	/**
	 * Referência para a classe de serviço de uma aplicação.
	 * 
	 * @see br.com.ecodif.service.ApplicationService
	 * @see br.com.ecodif.domain.Application
	 */
	@Inject
	protected ApplicationService applicationService;

	@Context
	protected ServletContext context;

	public ApplicationHelper() {
		super("Application");
	}

	/**
	 * Método responsável pela construção de <i>tags</i> no arquivo EMML
	 * associado a aplicação a fim de permitir a utilização dos <i>feeds</i>
	 * como variáveis de entrada para o <i>mashup</i>
	 * 
	 * @param application
	 *            - aplicação no qual o EMML será associado.
	 * @throws IOException
	 */
	public void buildEMMLInputFeeds(Application application) throws IOException {

		List<Environment> feeds = new ArrayList<Environment>();
		Iterator<Environment> it = application.getFeeds().iterator();
		while (it.hasNext()) {
			feeds.add(0, it.next());
		}

		String addressPortal = context.getInitParameter("AddressPortal");

		StringBuilder sb = new StringBuilder();
		sb.append("\n\t<variables>\n");
		for (Environment e : feeds) {
			sb.append("\t\t<variable name=\"feed" + e.getIddb()
					+ "\" type=\"document\" />\n");
		}
		sb.append("\t</variables>\n\n");

		for (Environment e : feeds) {
			sb.append("\t<directinvoke endpoint=\"" + addressPortal
					+ e.getWebsite()
					+ "\" method=\"GET\" outputvariable=\"$feed" + e.getIddb()
					+ "\" />\n");
		}
		sb.append("\n</mashup>");

		File file = new File(application.getEmmlReference());
		byte[] buffer = Files.readAllBytes(file.toPath());
		String stub = new String(buffer);
		stub = stub.replace("</mashup>", sb.toString());

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(application.getEmmlReference()),
				"ISO-8859-1"));
		out.write(stub);
		out.close();

		application.setUpdateDate((GregorianCalendar) GregorianCalendar
				.getInstance());

		applicationService.updateApplication(application);

	}

	/**
	 * Método responsável por desvincular um feed de uma aplicação;
	 * 
	 * @param feedid
	 *            - id do feed.
	 * @param app
	 *            - id da aplicação.
	 * @return TRUE se desvinculado com sucesso, FALSE caso contrário.
	 */
	public boolean unbindFeed(String feedid, Application app) {
		int id = Integer.parseInt(feedid);
		try {
			String addressPortal = context.getInitParameter("AddressPortal");
			File file = new File(app.getEmmlReference());
			byte[] buffer = Files.readAllBytes(file.toPath());
			String stub = new String(buffer);

			for (Environment feed : app.getFeeds()) {
				if (feed.getIddb() == id) {

					stub = stub.replace("\t\t<variable name=\"feed"
							+ addressPortal + feed.getIddb()
							+ "\" type=\"document\" />\n", "");
					stub = stub
							.replace(
									"\t<directinvoke endpoint=\""
											+ addressPortal
											+ feed.getWebsite()
											+ "\" method=\"GET\" outputvariable=\"$feed"
											+ feed.getIddb() + "\" />\n", "");

					app.getFeeds().remove(feed);
					BufferedWriter out = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(
									app.getEmmlReference()), "ISO-8859-1"));
					out.write(stub);
					out.close();

					app.setUpdateDate((GregorianCalendar) GregorianCalendar
							.getInstance());

					applicationService.updateApplication(app);
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/**
	 * Verifica se um arquivo EMML é válido, i.e. está em conformidade com o
	 * esquema XSD da linguagem EMML
	 * 
	 * @see {@link http://www.openmashup.org/schemas/v1.0/EMMLSpec.xsd}
	 * 
	 * @param emmlfile
	 *            Arquivo EMML a ser validado
	 * @return <code>true</code> (verdadeiro) se o arquivo EMML é válido,
	 *         <code>false</code> caso contrário
	 * 
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public boolean isValidEMML(String emmlfile) {
		try {
			String EMMLSchema = context.getInitParameter("EMMLSchema");
			Source schemaFile = new StreamSource(new File(EMMLSchema));

			InputStream inputStream = new FileInputStream(new File(emmlfile));
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource xmlFile = new InputSource(reader);
			xmlFile.setEncoding("UTF-8");

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(new SAXSource(xmlFile));
			return true;
		} catch (SAXException e) {
			System.out.println("Reason: " + e.getLocalizedMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Realiza o casamento entre os padrões presentes no arquivo EMML default
	 * pelas respectivas informações de cadastro da aplicação.
	 * 
	 * @param group
	 *            Padrão a ser substituido.
	 * @return Respectiva informação a substituir o padrão
	 */
	public String replaceWithValue(String group, Application application) {
		if (group.equals("#{app_name}")) {
			return application.getName();
		} else if (group.equals("#{app_description}")) {
			return application.getDescription();
		} else if (group.equals("#{author}")) {
			return application.getUser().getName();
		} else if (group.equals("#{author_email}")) {
			return application.getUser().getEmail();
		} else if (group.equals("#{app_tags}")) {
			return application.getTags();
		}

		return "";
	}

	/**
	 * Criação do arquivo EMML a partir dos dados de cadastro da aplicação
	 * 
	 * @param aplicação
	 *            que será criado o EMML
	 * @throws java.io.IOException
	 */
	protected void createEMMLFile(Application app) throws IOException {

		String emml = context.getInitParameter("EMMLDefaultFile");
		File file = new File(emml);
		byte[] buffer = Files.readAllBytes(file.toPath());
		String stub = new String(buffer);

		Pattern regex = Pattern.compile("#\\{([^}]*)\\}");
		Matcher regexMatcher = regex.matcher(stub);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(sb,
					replaceWithValue(regexMatcher.group(), app));
			i = regexMatcher.end();
		}

		String contents = sb.toString() + stub.substring(i);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(app.getEmmlReference()), "ISO-8859-1"));
		out.write(contents);
		out.close();
	}

}
