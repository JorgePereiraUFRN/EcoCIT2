package br.com.ecodif.searchcriteria;

import java.util.Date;

/**
 * Encapsula os critérios de busca utilizados para consulta por aplicações
 * @author Everton Cavalcante (evertonrsc@ppgsc.ufrn.br)
 */
public class ApplicationSearchCriteria 
{
	/** Nome da aplicação */
	private String name;
	
	/** Palavras-chave */
	private String tags;
	
	/** Nome ou login de usuário */
	private String user;
	
	/** Data-início de criação de aplicações */
	private Date startDate;
	
	/** Data-fim de criação de aplicações */
	private Date endDate;
	
	/** 
	 * Flag que indica se devem ser consideradas apenas aplicações atualizadas há menos
	 * de um mês
	 */
	private boolean updated;
	
	
	/** Construtor */
	public ApplicationSearchCriteria() {
		setName(null);
		setTags(null);
		setUser(null);
		setStartDate(null);
		setEndDate(null);
		setUpdated(false);		// Por padrão, são consideradas todas as aplicações
	}


	/**
	 * Retorna o nome da aplicação utilizado como critério de busca
	 * @return Nome da aplicação utilizado como critério de busca
	 */
	public String getName() {
		return name;
	}

	
	/**
	 * Modifica o nome da aplicação utilizado como critério de busca
	 * @param name Nome da aplicação para alteração
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * Retorna as palavras-chave utilizadas como critério de busca
	 * @return Palavras-chave utilizadas como critério de busca
	 */
	public String getTags() {
		return tags;
	}

	
	/**
	 * Modifica as palavras-chave utilizadas como critério de busca
	 * @param tags Palavras-chave para alteração
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	
	/**
	 * Retorna o nome ou login de usuário utilizado como critério de busca
	 * @return Nome ou login de usuário utilizado como critério de busca
	 */
	public String getUser() {
		return user;
	}

	
	/**
	 * Modifica o nome ou login de usuário utilizado como critério de busca
	 * @param user Nome ou login de usuário para alteração
	 */
	public void setUser(String user) {
		this.user = user;
	}

	
	/**
	 * Retorna a data-início de criação utilizada como critério de busca
	 * @return Data-início de criação utilizada como critério de busca
	 */
	public Date getStartDate() {
		return startDate;
	}

	
	/**
	 * Modifica a data-início de criação utilizada como critério de busca
	 * @param startDate Data-início de criação para alteração
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	
	/**
	 * Retorna a data-fim de criação utilizada como critério de busca
	 * @return Data-fim de criação utilizada como critério de busca
	 */
	public Date getEndDate() {
		return endDate;
	}

	
	/**
	 * Modifica a data-fim de criação utilizada como critério de busca
	 * @param endDate Data-fim de criação para alteração
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	
	/**
	 * Retorna o valor da flag que indica se devem ser consideradas apenas aplicações 
	 * atualizadas há menos de um mês
	 * @return <code>true</code> (verdadeiro), em caso afirmativo,
	 * 		   <code>false</code> (falso), em caso negativo
	 */
	public boolean isUpdated() {
		return updated;
	}

	
	/**
	 * Modifica o valor da flag que indica se devem ser consideradas apenas aplicações 
	 * atualizadas há menos de um mês
	 * @param updated Flag para alteração
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}
