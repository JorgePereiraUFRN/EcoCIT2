package br.com.ecodif.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

public class FileHelper {

	/**
	 * Copia um determinado conteudo em um arquivo
	 * 
	 * @param in
	 *            Conteudo a ser copiado para o arquivo especificado
	 * @param fileName
	 *            Caminho do arquivo de origem
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
	
	public void copyFile(String from, String to){
		File source = new File(from);
		File dest = new File(to);
		try {
		    FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

}
