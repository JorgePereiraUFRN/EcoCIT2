package br.com.ecodif.mb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import br.com.ecodif.dao.ApplicationDAO;
import br.com.ecodif.dao.UserDAO;
import br.com.ecodif.domain.Application;
import br.com.ecodif.domain.Eeml;
import br.com.ecodif.domain.Environment;
import br.com.ecodif.domain.User;
import br.com.ecodif.eeml_contract.Eeml_Contract;
import br.com.ecodif.framework.EemlManager;
import br.com.ecodif.searchcriteria.ApplicationSearchCriteria;

/**
 * <i>Managed bean</i> que intermedia a comunica��o entre a interface Web e as
 * classes relacionadas � l�gica de neg�cio referente a aplica��es
 * @author Everton Cavalcante (evertonrsc@ppgsc.ufrn.br)
 */
@ManagedBean
@ViewScoped
public class ApplicationMB {

	/**
	 * Refer�ncia � classe de acesso a dados associada a aplica��es
	 * @see br.com.ecodif.dao.ApplicationDAO
	 */
	@EJB
	private ApplicationDAO applicationDAO;

	/**
	 * Refer�ncia � classe de acesso a dados associada a usu�rios
	 * @see br.com.ecodif.dao.UserDAO
	 */
	@EJB
	private UserDAO userDAO;

	/**
	 * Refer�ncia ao <i>managed bean</i> associado a usu�rios
	 * @see br.com.ecodif.mb.UserMB
	 */
	@ManagedProperty("#{userMB}")
	private UserMB userMB;

	/**
	 * Refer�ncia ao <i>managed bean</i> associado a <i>feeds</i>
	 * @see br.com.ecodif.mb.EemlMB
	 */
	@ManagedProperty("#{eemlMB}")
	private EemlMB eemlMB;

	/**
	 * Aplica��o em quest�o
	 * @see br.com.ecodif.domain.Application
	 */
	private Application application;

	/**
	 * Crit�rios de busca utilizados para consulta
	 * @see br.com.ecodif.searchcriteria.ApplicationSearchCriteria
	 */
	private ApplicationSearchCriteria criteria;

	/**
	 * Arquivo informado pelo usu�rio para <i>upload</i>
	 * @see org.primefaces.model.UploadedFile
	 */
	private UploadedFile applicationFile;

	/** Lista que armazena resultados de consultas por aplica��es */
	private List<Application> applicationsAll;

	/** Lista de aplica��es pertencentes ao usu�rio */
	private List<Application> applicationsUser;

	/** Objeto que armazena o resultado da execu��o de uma aplica��o */
	private String resultExecution = new String("");

	/**
	 * Objeto <code>HashMap</code> que armazena a sele��o de <i>checkboxes</i>
	 * na pagina Web, contendo o identificador do <i>feed</i> a ser inclu�do na
	 * aplica��o e uma flag indicando se esse objeto foi selecionado ou n�o
	 */
	private Map<Long, Boolean> inclusionFeeds;

	/**
	 * Objeto <code>HashMap</code> que armazena a sele��o de <i>checkboxes</i>
	 * na pagina Web, contendo o identificador do <i>feed</i> a ser exclu�do da
	 * aplica��o e uma flag indicando se esse objeto foi selecionado ou n�o
	 */
	private Map<Long, Boolean> exclusionFeeds;

	private Eeml_Contract eemlContract;

	private Eeml eeml;

	@EJB
	private EemlManager eemlManager;

	
	/** M�todo invocado na instancia��o do <i>managed</i> bean */
	@PostConstruct
	public void init() {

		/*
		 * Para acessar o objeto da classe Application proveniente de outra
		 * p�gina, � utilizado um objeto HttpSession
		 */
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = (HttpSession) request.getSession();
			
			if (session.getAttribute("application") != null) {
				this.setApplication((Application) session
						.getAttribute("application"));
			}
		} catch (NoResultException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		/*
		 * Para acessar o objeto referente ao resultado da execu��o de
		 * aplica��o, realizada em outra p�gina, � utilizado um objeto
		 * HttpSession
		 */
		try {
			HttpServletRequest request = (HttpServletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpSession session = (HttpSession) request.getSession();
			
			if (session.getAttribute("resultExecution") != null) {
				this.setResultExecution((String) session
						.getAttribute("resultExecution"));
			}
		} catch (NoResultException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		/*
		 * Lista previamente carregada com todas as aplica��es p�blicas
		 * registradas
		 */
		if (applicationsAll == null) {
			this.setAllApplications(applicationDAO.findPublicApplications(false));
		}
	}

	public Eeml_Contract getEemlContract() {
		if (eemlContract == null)
			eemlContract = new Eeml_Contract();
		return eemlContract;
	}

	public void setEemlContract(Eeml_Contract eemlContract) {
		this.eemlContract = eemlContract;
	}

	public Eeml getEeml() {
		if (eeml == null)
			eeml = new Eeml();
		return eeml;
	}

	public void setEeml(Eeml eeml) {
		this.eeml = eeml;
	}

	/**
	 * Retorna a aplica��o em quest�o 
	 * @return Aplica��o em quest�o
	 */
	public Application getApplication() {
		if (application == null) {
			application = new Application();
		}
		return application;
	}

	/**
	 * Modifica a aplica��o em quest�o
	 * @param application Objeto da classe <code>Application</code> para altera��o
	 */
	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Retorna os crit�rios de busca utilizados, agregados como um objeto da
	 * classe <code>ApplicationSearchCriteria</code>
	 * @return Crit�rios de busca utilizados
	 */
	public ApplicationSearchCriteria getCriteria() {
		if (criteria == null) {
			criteria = new ApplicationSearchCriteria();
		}
		return criteria;
	}

	/**
	 * Modifica os crit�rios de busca
	 * @param criteria Objeto da classe <code>ApplicationSearchCriteria</code> 
	 * 			para altera��o
	 */
	public void setCriteria(ApplicationSearchCriteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * Modifica a refer�ncia para o <i>managed bean</i> associado a usu�rios
	 * @param userMB Objeto da classe <code>UserMB</code> para altera��o
	 */
	public void setUserMB(UserMB userMB) {
		this.userMB = userMB;
	}

	/**
	 * Modifica a refer�ncia para o <i>managed bean</i> associado a <i>feeds</i>
	 * @param userMB Objeto da classe <code>EemlMB</code> para altera��o
	 */
	public void setEemlMB(EemlMB eemlMB) {
		this.eemlMB = eemlMB;
	}

	/**
	 * Retorna o arquivo informado pelo usu�rio para <i>upload</i>
	 * @return Arquivo informado pelo usu�rio para <i>upload</i>
	 */
	public UploadedFile getApplicationFile() {
		return applicationFile;
	}

	/**
	 * Modifica o arquivo informado pelo usu�rio para <i>upload</i>
	 * @param applicationFile Arquivo para altera��o
	 */
	public void setApplicationFile(UploadedFile applicationFile) {
		this.applicationFile = applicationFile;
	}

	/**
	 * Retorna a lista que armazena resultados de consultas por aplica��es
	 * @return Lista que armazena resultados de consultas por aplica��es
	 */
	public List<Application> getApplicationsAll() {
		return applicationsAll;
	}

	/**
	 * Modifica a lista que armazena resultados de consultas por aplica��es
	 * @param applications Lista de aplica��es para altera��o
	 */
	public void setAllApplications(List<Application> applications) {
		this.applicationsAll = applications;
	}

	/**
	 * Retorna a lista de aplica��es pertencentes ao usu�rio em quest�o, dado
	 * pelo objeto <code>userMB</code>
	 * @return Lista de aplica��es pertencentes ao usu�rio
	 */
	public List<Application> getApplicationsUser() {
		User user = userDAO.findByLogin(userMB.getUserName());
		if (applicationsUser == null) {
			this.setApplicationsUser(applicationDAO
					.findApplicationsByUserId(user.getId()));
		}
		return applicationsUser;
	}

	/**
	 * Modifica a lista de aplica��es pertencentes ao usu�rio
	 * @param applicationsUser Lista de aplica��es para altera��o
	 */
	public void setApplicationsUser(List<Application> applicationsUser) {
		this.applicationsUser = applicationsUser;
	}

	/**
	 * Retorna o objeto que armazena o resultado da execu��o de uma aplica��o
	 * @return Objeto que armazena o resultado da execu��o de uma aplica��o
	 */
	public String getResultExecution() {
		return resultExecution;
	}

	/**
	 * Modifica o objeto que armazena o resultado da execu��o de uma aplica��o
	 * @param resultExecution Objeto para altera��o
	 */
	public void setResultExecution(String resultExecution) {
		this.resultExecution = resultExecution;
	}

	/**
	 * Retorna o <code>HashMap</code> que armazena a sele��o de
	 * <i>checkboxes</i> na pagina Web
	 * @return Objeto com a sele��o de <i>checkboxes</i>
	 */
	public Map<Long, Boolean> getInclusionFeeds() {
		if (inclusionFeeds == null) {
			inclusionFeeds = new HashMap<Long, Boolean>();
		}
		return inclusionFeeds;
	}

	/**
	 * Modifica o <code>HashMap</code> que armazena a sele��o de
	 * <i>checkboxes</i> na pagina Web
	 * @param checked Objeto para altera��o
	 */
	public void setInclusionFeeds(Map<Long, Boolean> checked) {
		this.inclusionFeeds = checked;
	}

	/**
	 * Retorna o <code>HashMap</code> que armazena a sele��o de
	 * <i>checkboxes</i> na pagina Web
	 * @return Objeto com a sele��o de <i>checkboxes</i>
	 */
	public Map<Long, Boolean> getExclusionFeeds() {
		if (exclusionFeeds == null) {
			exclusionFeeds = new HashMap<Long, Boolean>();
		}
		return exclusionFeeds;
	}

	/**
	 * Modifica o <code>HashMap</code> que armazena a sele��o de
	 * <i>checkboxes</i> na pagina Web
	 * @param checked Objeto para altera��o
	 */
	public void setExclusionFeeds(Map<Long, Boolean> checked) {
		this.exclusionFeeds = checked;
	}

	/**
	 * Realiza uma busca por aplica��es com base em crit�rios de busca,
	 * especificados pelo objeto <code>criteria</code> da classe
	 * <code>ApplicationSearchCriteria</code>
	 */
	public void searchApplications() {

		/*
		 * Se apenas uma das datas especificadas para consulta por data de
		 * cria��o � informada, � exibida uma mensagem de erro para o usu�rio
		 */
		if ((criteria.getStartDate() != null && criteria.getEndDate() == null)
				|| (criteria.getStartDate() == null && criteria.getEndDate() != null)) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Por favor, informe as duas datas da faixa de busca."));
			return;

		} else {

			/*
			 * Se ambas as datas para a consulta s�o informadas, por�m a segunda
			 * data (data-fim) � anterior � primeira data (data-in�cio), �
			 * exibida uma mensagem de erro exibida quando o usu�rio
			 */
			if (criteria.getStartDate() != null
					&& criteria.getEndDate() != null
					&& criteria.getEndDate().before(criteria.getStartDate())) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								"As datas informadas s�o inv�lidas. Por favor, tente novamente."));
				return;

			} else {

				/*
				 * Realiza a busca por aplica��es com base nos crit�rios de
				 * busca especificados
				 */
				List<Application> apps = new ArrayList<Application>();
				Iterator<Application> it = applicationDAO
						.findApplicationsByCriteria(criteria).iterator();
				while (it.hasNext()) {
					apps.add(0, it.next());
				}

				this.setAllApplications(apps);
			}
		}
	}

	/**
	 * Retorna um objeto <code>StreamedContent</code> associado a um arquivo
	 * para <i>download</i>
	 * @see {@link http://www.primefaces.org/showcase/ui/fileDownload.jsf}
	 * 
	 * @return Objeto <code>StreamedContent</code> associado a um arquivo para
	 *         <i>download</i>
	 */
	public StreamedContent getFile() {
		StreamedContent file = null;
		if (this.getApplication().getId() != 0) {
			try {
				File fileDownload = new File(this.getApplication()
						.getEmmlReference());
				FileInputStream stream = new FileInputStream(fileDownload);
				file = new DefaultStreamedContent(stream, "",
						fileDownload.getName());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		return file;
	}

	/** Salva (persiste) uma aplica��o */
	public void saveApplication() {
		try {

			/*
			 * Se o nome da aplica��o n�o for informado no formul�rio de
			 * cadastro, � exibida uma mensagem de erro para o usu�rio
			 */
			if (application.getName().isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Por favor, informe o nome da aplica��o a ser cadastrada e tente novamente."));
				return;
			}

			// Usu�rio em quest�o
			User user = userDAO.findByLogin(userMB.getUserName());
			application.setUser(user);

			/* 
			 * Refer�ncia para o arquivo EMML a ser criado automaticamente no
			 * diret�rio de aplica��es
			 */
			String fileNameRep = FacesContext.getCurrentInstance()
					.getExternalContext()
					.getInitParameter("ApplicationsDirectory")
					+ userMB.getUserName() + "_" + application.getName() + ".emml";
		
			application.setEmmlReference(userMB.getUserName() + "_" + application.getName() + ".emml");

			/*
			 * Cria��o autom�tica de arquivo EMML a partir dos dados de cadastro
			 * da aplica��o
			 */
			createEMMLFile();

			if (applicationFile != null) {
				// Copia do arquivo para o diret�rio de aplica��es
				copyFile(applicationFile.getInputstream(), fileNameRep);
				
				byte[] content = IOUtils.toByteArray(applicationFile.getInputstream());
				Blob blob = new SerialBlob(content);
				application.setEmmlFile(blob);
				
				
				
				
				/* 
				 * Refer�ncia para o arquivo EMML a ser criado automaticamente 
				 * no diret�rio do motor de execu��o
				 */
				/*String fileNameEng = FacesContext.getCurrentInstance()
						.getExternalContext()
						.getInitParameter("EMMLEnginePath") + File.separator +
						userMB.getUserName() + "_" + application.getName() + ".emml";*/
				
				// Copia do arquivo para o diret�rio do motor de execu��o
				//copyFile(applicationFile.getInputstream(), fileNameEng);

				/*
				 * Se o arquivo EMML importado pelo usu�rio for inv�lido (i.e.
				 * n�o estiver em conformidade com o esquema da linguagem EMML,
				 * � exibida uma mensagem de erro para o usu�rio
				 */
				if (!isValidEMML(fileNameRep)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								"O arquivo EMML a ser importado � inv�lido. Por favor, tente novamente."));

					File fileDelete = new File(fileNameRep);
					fileDelete.delete();

					return;
				}
			}

			/*
			 * Se j� existir uma aplica��o cadastrada com o nome informado no
			 * formul�rio, � exibida uma mensagem de erro para o usu�rio
			 */
			if (application.getId() == 0) {
				if (!application.getName().isEmpty()
						&& applicationDAO.findApplicationByName(application
								.getName()) != null) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								"J� existe uma aplica��o cadastrada com o nome informado. Por favor, tente novamente."));
					return;
				}

				// Datas de cria��o e atualiza��o setadas como data/hora atual
				GregorianCalendar gc = (GregorianCalendar) GregorianCalendar
						.getInstance();
				application.setCreationDate(gc);
				application.setUpdateDate(gc);

				
				
				// Cria��o da aplica��o
				applicationDAO.save(application);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Aplica��o criada com sucesso."));

			} else {
				// Data de atualiza��o setada como data/hora atual
				application.setUpdateDate((GregorianCalendar) GregorianCalendar
						.getInstance());

				System.out.println("APPLICAÇÃO "+application.getEmmlFile().length());
				
				// Atualiza��o da aplica��o
				applicationDAO.update(application);
				
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								"Aplica��o atualizada com sucesso."));
			}
			
			
			File app = new File(fileNameRep);
			app.delete();
			

			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					"Erro ocorrido ao tentar cadastrar a aplica��o. Por favor, tente novamente."));
		} finally {
			
			
			
			FacesContext fc = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) fc.getExternalContext().
					getSession(false);
			session.setAttribute("application", application);
		}
	}

	/**
	 * Cria��o autom�tica de arquivo EMML a partir dos dados de cadastro da
	 * aplica��o
	 * @throws java.io.IOException
	 */
	public void createEMMLFile() throws IOException {

		// Cria��o de conte�do a partir do arquivo EMML default
		File file = new File(FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("EMMLDefaultFile"));
		byte[] buffer = Files.readAllBytes(file.toPath());
		String stub = new String(buffer);

		/*
		 * Substutui��o dos padr�es do arquivo default pelas informa��es
		 * fornecidas no cadastro
		 */
		Pattern regex = Pattern.compile("#\\{([^}]*)\\}");
		Matcher regexMatcher = regex.matcher(stub);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (regexMatcher.find()) {
			regexMatcher.appendReplacement(sb,
					replaceWithValue(regexMatcher.group()));
			i = regexMatcher.end();
		}
		
		String fileNameRep = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getInitParameter("ApplicationsDirectory")+application.getEmmlReference();

		// Escrita do conte�do no respectivo arquivo EMML associado � aplica��o
		String contents = sb.toString() + stub.substring(i);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileNameRep),
				"ISO-8859-1"));
		out.write(contents);
		out.close();
	}

	/**
	 * Realiza o casamento entre os padr�es presentes no arquivo EMML default
	 * pelas respectivas informa��es de cadastro da aplica��o
	 * @param group Padr�o a ser substitu�do
	 * @return Respectiva informa��o a substituir o padr�o
	 */
	private String replaceWithValue(String group) {
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
	 * Verifica se um arquivo EMML � v�lido, i.e. est� em conformidade com o
	 * esquema XSD da linguagem EMML 
	 * @see {@link http://www.openmashup.org/schemas/v1.0/EMMLSpec.xsd}
	 * 
	 * @param emmlfile Arquivo EMML a ser validado
	 * @return <code>true</code> (verdadeiro) se o arquivo EMML � v�lido,
	 *         <code>false</code> caso contr�rio
	 * 
	 * @throws org.xml.sax.SAXException
	 * @throws java.io.IOException
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	public boolean isValidEMML(String emmlfile) {
		try {
			// Refer�ncia para o esquema XSD da linguagem EMML
			String EMMLSchema = FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLSchema");
			Source schemaFile = new StreamSource(new File(EMMLSchema));
						
			// Estrutura baseada em XML a ser validada
			InputStream inputStream = new FileInputStream(new File(emmlfile));
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource xmlFile = new InputSource(reader);
			xmlFile.setEncoding("UTF-8");
				
			// Valida��o frente ao esquema XSD
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
	 * Redireciona para a p�gina p�blica de visualiza��o de detalhes de
	 * aplica��o
	 * @param currentApplication Aplica��o em quest�o
	 * @return Refer�ncia para a p�gina p�blica de visualiza��o de detalhes de
	 *         aplica��o
	 * @see /WEB-INF/faces-config.xml
	 */
	public String viewApplicationPublic(Application currentApplication) {

		/*
		 * Passagem do objeto referente � aplica��o em quest�o para a p�gina de
		 * visualizacao
		 */
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", currentApplication);
		return "viewapplication";
	}

	/**
	 * Redireciona para a p�gina protegida de visualiza��o de detalhes de
	 * aplica��o
	 * 
	 * @param currentApplication Aplica��o em quest�o
	 * @return Refer�ncia para a p�gina protegida de visualiza��o de detalhes de
	 *         aplica��o
	 * @see /WEB-INF/faces-config.xml
	 */
	public String viewApplicationDevApp(Application currentApplication) {

		/*
		 * Passagem do objeto referente � aplica��o em quest�o para a p�gina de
		 * visualiza��o
		 */
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().
				getSession(false);
		session.setAttribute("application", currentApplication);
		return "viewapplicationdevapp";
	}

	/**
	 * Redireciona para a p�gina de edi��o de aplica��o
	 * @return Refer�ncia para a p�gina de edi��o de aplica��o
	 * @see /WEB-INF/faces-config.xml
	 */
	public String editApplication() {

		/*
		 * Passagem do objeto referente � aplica��o em quest�o para a p�gina de
		 * edi��o
		 */
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().
				getSession(false);
		session.setAttribute("application", application);
		return "cadapplication";
	}

	/**
	 * Realiza a exclus�o da aplica��o em quest�o
	 * @see /WEB-INF/faces-config.xml
	 */
	public void deleteApplication() {
		try {
			// Caminho do arquivo EMML associado � aplica��o a ser exclu�da
			String emmlFile = application.getEmmlReference();

			/*
			 * Exclus�o da aplica��o em quest�o da respectiva tabela no banco
			 * dados
			 */
			applicationDAO.delete(application);

			// Remo��o da aplica��o em quest�o da lista de aplica��es do usu�rio
			this.getApplicationsUser().remove(application);

			// Exclus�o do arquivo EMML do diret�rio de aplica��es
			File fileRep = new File(emmlFile);
			fileRep.delete();
			
			/* 
			 * Refer�ncia para o arquivo EMML a ser criado automaticamente 
			 * no diret�rio do motor de execu��o
			 */
			String fileNameEng = FacesContext.getCurrentInstance()
					.getExternalContext()
					.getInitParameter("EMMLEnginePath") + File.separator +
					userMB.getUserName() + "_" + application.getName() + ".emml";
			
			// Exclus�o do arquivo EMML do diret�rio do motor de execu��o
			File fileEng = new File(fileNameEng);
			fileEng.delete();

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							"Aplica��o exclu�da com sucesso."));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							"Erro ocorrido ao tentar excluir a aplica��o"));
		}
	}

	/**
	 * Execu��o de aplica��o (<i>mashup</i> EMML) em �rea p�blica
	 * @param currentApplication Aplica��o a ser executada
	 * @return Refer�ncia para a p�gina p�blica de apresenta��o de resultados da
	 *         execu��o
	 */
	public String executeApplicationPublic(Application currentApplication) {
		
		String filePath = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getInitParameter("EMMLEnginePath") + File.separator +
				currentApplication.getEmmlReference();
		
		Blob blob = currentApplication.getEmmlFile();
		
		try{
		
		byte[] fileBytes = blob.getBytes(1, (int) blob.length());
		
		FileOutputStream outputStream = new FileOutputStream(filePath);
		outputStream.write(fileBytes);
		outputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		String emmlreference = currentApplication.getEmmlReference();
		
		String appname = currentApplication.getEmmlReference(); /*emmlreference.substring(
				emmlreference.lastIndexOf(File.separator) + 1,
				emmlreference.lastIndexOf("."));*/

		StringBuilder sb = new StringBuilder();
		try {
			// Chamada � aplica��o no motor de execu��o EMML
			URL url = new URL(FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLEngineURL")
					+ appname);

			InputStream execution = url.openStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					execution, "ISO-8859-1"));

			// Constru��o do conte�do resultante da execu��o da aplica��o
			String str = "";
			while ((str = in.readLine()) != null) {
				sb.append(str + "\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setResultExecution(sb.toString());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", currentApplication);
		session.setAttribute("resultExecution", resultExecution);

		return "resultexecution";
	}

	/**
	 * Execu��o de aplica��o (<i>mashup</i> EMML) em �rea protegida
	 * @param currentApplication Aplica��o a ser executada
	 * @return Refer�ncia para a p�gina protegida de apresenta��o de resultados
	 *         da execu��o
	 */
	public String executeApplicationDevApp(Application currentApplication) {
		
		String filePath = FacesContext.getCurrentInstance()
				.getExternalContext()
				.getInitParameter("EMMLEnginePath") + File.separator +
				currentApplication.getEmmlReference();
		
		Blob blob = currentApplication.getEmmlFile();
		
		try{
		
		byte[] fileBytes = blob.getBytes(1, (int) blob.length());
		
		FileOutputStream outputStream = new FileOutputStream(filePath);
		outputStream.write(fileBytes);
		outputStream.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String emmlreference = currentApplication.getEmmlReference();
		String appname = currentApplication.getEmmlReference(); /*emmlreference.substring(
				emmlreference.lastIndexOf(File.separator) + 1,
				emmlreference.lastIndexOf("."));*/

		StringBuilder sb = new StringBuilder();
		try {
			// Chamada � aplica��o no motor de execu��o EMML
			URL url = new URL(FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLEngineURL")
					+ appname);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "ISO-8859-1"));

			// Constru��o do conte�do resultante da execu��o da aplica��o
			String str = "";
			while ((str = in.readLine()) != null) {
				sb.append(str + "\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.setResultExecution(sb.toString());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", currentApplication);
		session.setAttribute("resultExecution", resultExecution);

		return "resultexecutiondevapp";
	}

	/**
	 * Copia um determinado conte�do em um arquivo
	 * @param in Conte�do a ser copiado para o arquivo especificado
	 * @param fileName Caminho do arquivo de origem
	 */
	public void copyFile(InputStream in, String fileName) {
		try {
			OutputStream out = new FileOutputStream(new File(fileName));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Copia o conte�do especificado em um arquivo de origem para um arquivo de
	 * destino
	 * @param origin Caminho do arquivo de origem
	 * @param target Caminho do arquivo de destino
	 */
	public void copyFile(String origin, String target) {
		try {
			
			File file = new File(origin);
			byte[] buffer = Files.readAllBytes(file.toPath());
			String stub = new String(buffer);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(target), "ISO-8859-1"));
			out.write(stub);
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Redireciona para a p�gina de vincula��o de <i>feeds</i>
	 * @return Refer�ncia para a p�gina de vincula��o de <i>feeds</i>
	 * @see /WEB-INF/faces-config.xml
	 */
	public String includeFeeds() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", application);
		return "includefeeds";
	}

	/** Vincula��o de <i>feeds</i> � aplica��o em quest�o */
	public void bindFeeds() {
		try {

			// Inclus�o dos feeds selecionados na p�gina Web � aplica��o em
			// quest�o
			for (Environment e : eemlMB.getAllEnvironments()) {
				if (inclusionFeeds.get(e.getIddb())) {
					application.getFeeds().add(e);
				}
			}

			/*
			 * Constru��o de tags no arquivo EMML associado � aplica��o a fim de
			 * permitir a utiliza��o dos feeds como vari�veis de entrada para o
			 * mashup
			 */
			buildEMMLInputFeeds();

			// Data de atualiza��o setada como data/hora atual
			application.setUpdateDate((GregorianCalendar) GregorianCalendar
					.getInstance());

			// Atualiza��o da aplica��o
			applicationDAO.update(application);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						"Os feeds selecionados foram inclu�dos com sucesso na aplica��o."));

			inclusionFeeds.clear();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					"Erro ocorrido ao tentar incluir os feeds selecionados na aplica��o."));
		}
	}

	/**
	 * Constru��o de <i>tags</i> no arquivo EMML associado � aplica��o a fim de
	 * permitir a utiliza��o dos <i>feeds</i> como vari�veis de entrada para o
	 * <i>mashup</i>
	 * @throws IOException
	 */
	private void buildEMMLInputFeeds() throws IOException {

		// Feeds da aplica��o
		List<Environment> feeds = new ArrayList<Environment>();
		Iterator<Environment> it = application.getFeeds().iterator();
		while (it.hasNext()) {
			feeds.add(0, it.next());
		}

		String addressPortal = FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("AddressPortal");

		/*
		 * Constru��o das tags referentes �s vari�veis de entrada do mashup
		 * (<variable>), uma para cada feed vinculado � aplica��o. Os nomes das
		 * vari�veis s�o convencionados como feed<id>, onde <id> � o
		 * identificador do feed em quest�o.
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t<variables>\n");
		for (Environment e : feeds) {
			sb.append("\t\t<variable name=\"feed" + e.getIddb()
					+ "\" type=\"document\" />\n");
		}
		sb.append("\t</variables>\n\n");

		/*
		 * Constru��o das tags referentes � obten��o via requisi��o HTTP GET do
		 * conte�do dos feeds vinculados � aplica��o (<directinvoke>), conte�do
		 * esse que ser� armazenado nas respectivas vari�veis referentes aos
		 * feeds
		 */
		for (Environment e : feeds) {
			sb.append("\t<directinvoke endpoint=\"" + addressPortal
					+ e.getWebsite()
					+ "\" method=\"GET\" outputvariable=\"$feed" + e.getIddb()
					+ "\" />\n");
		}
		sb.append("\n</mashup>");

		/*
		 * Atualiza��o autom�tica do arquivo EMML da aplica��o com as tags
		 * constru�das
		 */
		File file = new File(application.getEmmlReference());
		byte[] buffer = Files.readAllBytes(file.toPath());
		String stub = new String(buffer);
		stub = stub.replace("</mashup>", sb.toString());

		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(application.getEmmlReference()),
				"ISO-8859-1"));
		out.write(stub);
		out.close();
	}

	/**
	 * Redireciona para a p�gina de desvincula��o de <i>feeds</i>
	 * @return Refer�ncia para a p�gina de desvincula��o de <i>feeds</i>
	 * @see /WEB-INF/faces-config.xml
	 */
	public String deleteFeeds() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext()
				.getSession(false);
		session.setAttribute("application", application);
		return "deletefeeds";
	}

	/** Desvincula��o de <i>feeds</i> da aplica��o em quest�o */
	public void unbindFeeds() {
		try {

			String addressPortal = FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("AddressPortal");

			/*
			 * Carregamento do conte�do atual do arquivo EMML associado �
			 * aplica��o
			 */
			File file = new File(application.getEmmlReference());
			byte[] buffer = Files.readAllBytes(file.toPath());
			String stub = new String(buffer);

			for (Environment e : application.getFeeds()) {
				if (exclusionFeeds.get(e.getIddb())) {

					// Exclus�o das tags associadas ao feed a ser exclu�do
					stub = stub.replace("\t\t<variable name=\"feed"
							+ addressPortal + e.getIddb()
							+ "\" type=\"document\" />\n", "");
					stub = stub.replace("\t<directinvoke endpoint=\""
								+ addressPortal + e.getWebsite()
								+ "\" method=\"GET\" outputvariable=\"$feed"
								+ e.getIddb() + "\" />\n", "");

					// Remo��o efetiva dos feeds selecionados para desvincula��o
					application.getFeeds().remove(e);
				}
			}

			// Atualiza��o do arquivo EMML associado � aplica��o
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(application.getEmmlReference()),
					"ISO-8859-1"));
			out.write(stub);
			out.close();

			// Data de atualiza��o setada como data/hora atual
			application.setUpdateDate((GregorianCalendar) GregorianCalendar
					.getInstance());

			// Atualiza��o da aplica��o
			applicationDAO.update(application);

			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
					"Os feeds selecionados foram inclu�dos com sucesso na aplica��o."));

			exclusionFeeds.clear();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
					"Erro ocorrido ao tentar incluir os feeds selecionados na aplica��o."));
		}
	}

	/**
	 * Retorna uma lista com os <i>feeds</i> atualmente vinculados � aplica��o
	 * @return Lista com <i>feeds</i> atualmente vinculados � aplica��o
	 */
	public List<Environment> getFeeds() {
		List<Environment> feeds = new ArrayList<Environment>();
		Iterator<Environment> it = application.getFeeds().iterator();
		while (it.hasNext()) {
			feeds.add(0, it.next());
		}

		return feeds;
	}
	
	public String getContentsEmmlFile() {
		String contents = "";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(application
					.getEmmlReference()));
			while (in.ready()) {
				contents += in.readLine() + "\n";
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return contents;
	}

	/**
	 * Verifica se a aplica��o em quest�o j� est� cadastrada
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationRegistered() {
		return !(this.getApplication().getId() == 0);
	}

	/**
	 * Verifica se o <em>upload</em> do arquivo associado � aplica��o foi 
	 * realizado com sucesso
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationFileUploaded() {
		if (this.getApplication().getId() == 0) {
			return false;
		} else {
			return !(this.applicationDAO.find(this.getApplication().getId())
					.getEmmlReference() == null);
		}
	}

	/**
	 * Verifica se a lista de aplica��es como resultados de consultas por
	 * aplica��es est� vazia
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationsAllEmpty() {
		return applicationsAll.isEmpty();
	}

	/**
	 * Verifica se a lista de aplica��es pertencentes ao usu�rio em quest�o est�
	 * vazia
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isApplicationsUserEmpty() {
		User user = userDAO.findByLogin(userMB.getUserName());
		return applicationDAO.findApplicationsByUserId(user.getId()).isEmpty();
	}

	/**
	 * Verifica se o conjunto de <i>feeds</i> atualmente vinculados � aplica��o
	 * est� vazio
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 *         <code>false</code> (falso), em caso negativo
	 */
	public boolean isFeedsListEmpty() {
		return application.getFeeds().isEmpty();
	}
	

	// FIXME Este metodo futuramente sera removido desta classe
	public Eeml createApplicationFromFeeds() {
		Eeml eeml = new Eeml();

		// Lista com os endere�os dos feeds
		List<String> feedsWebSites = new ArrayList<String>();
		feedsWebSites
				.add("http://localhost:8080/EcodifAPI/api/feeds/1/datastreams/1");
		feedsWebSites
				.add("http://localhost:8080/EcodifAPI/api/feeds/2/datastreams/2");

		String filepath = FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("ApplicationsDirectory")
				+ "tempmashup.emml";

		// Criacao de mashup EMML temporario que agrega os feeds
		createTempMashup(feedsWebSites, filepath);

		/*
		 * Copia do conte�do do arquivo EMML criado para um novo arquivo
		 * presente no diret�rio de deploy de aplica��es no motor de execu��o
		 * EMML
		 */
		String fileExecute = FacesContext.getCurrentInstance()
				.getExternalContext().getInitParameter("EMMLEnginePath")
				+ "/tempmashup.emml";
		copyFile(filepath, fileExecute);

		try {

			// Chamada � aplica��o no motor de execu��o EMML
			URL url = new URL(FacesContext.getCurrentInstance()
					.getExternalContext().getInitParameter("EMMLEngineURL")
					+ "tempmashup");
			InputStream execute = url.openStream();

			/*
			 * Convers�o do resultado da execu��o do mashup para um objeto EEML,
			 * conforme o contrato do protocolo
			 */
			Eeml_Contract contract = eemlManager
					.read_eemlFromInputStream(execute);
			eeml = eemlManager.eemlContractToDomain(contract);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return eeml;
	}

	// FIXME Este metodo futuramente sera removido desta classe
	private void createTempMashup(List<String> feedsWebSites, String filepath) {
		StringBuilder sb = new StringBuilder();

		// Cabe�alho do mashup EMML
		String header = "<mashup"
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xsi:schemaLocation=\"http://www.openemml.org/2009-04-15/EMMLSchema"
				+ " ../schemas/EMMLSpec.xsd\""
				+ " xmlns=\"http://www.openemml.org/2009-04-15/EMMLSchema\""
				+ " name=\"tempmashup\">\n";
		sb.append(header);

		// Declara��o da vari�vel de sa�da do mashup EMML
		sb.append("\n\t<output name=\"result\" type=\"document\" />\n");

		int numfeeds = feedsWebSites.size();
		if (numfeeds > 0) {
			/*
			 * Constru��o das tags referentes �s vari�veis de entrada do mashup
			 * (<variable>), uma para cada feed
			 */
			sb.append("\n\t<variables>\n");
			for (int i = 0; i < numfeeds; i++) {
				sb.append("\t\t<variable name=\"feed" + (i + 1)
						+ "\" type=\"document\" />\n");
			}
			sb.append("\t</variables>\n\n");

			/*
			 * Constru��o das tags referentes � obten��o via requisi��o HTTP GET
			 * do conte�do dos feeds (<directinvoke>), conte�do esse que ser�
			 * armazenado nas respectivas vari�veis referentes aos feeds
			 */
			for (int i = 0; i < numfeeds; i++) {
				sb.append("\t<directinvoke endpoint=\"" + feedsWebSites.get(i)
						+ "\" method=\"GET\" outputvariable=\"$feed" + (i + 1)
						+ "\" />\n");
			}

			// Agrega��o dos conte�dos dos feeds
			if (numfeeds == 1) {
				String assign = "\n\t<assign fromvariable=\"$feed1\""
						+ " outputvariable=\"$result\" />";
				sb.append(assign);
			} else {
				String merge = "\n\t<merge inputvariables=\"";
				for (int i = 0; i < numfeeds; i++) {
					merge += (i == (numfeeds - 1)) ? ("$feed" + (i + 1) + "\" ")
							: ("$feed" + (i + 1) + ", ");
				}
				merge += "outputvariable=\"$result\" />";
				sb.append(merge);
			}
		}

		sb.append("\n</mashup>");

		try {
			// Opera��o de escrita do arquivo temporario
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filepath), "ISO-8859-1"));
			out.write(sb.toString());
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
