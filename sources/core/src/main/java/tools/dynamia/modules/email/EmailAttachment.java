package tools.dynamia.modules.email;

import java.io.File;

public class EmailAttachment {

	private String name;
	private File file;

	public EmailAttachment(File file) {
		super();
		this.file = file;
		this.name = file.getName();
	}

	public EmailAttachment(String name, File file) {
		super();
		this.name = name;
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

}
