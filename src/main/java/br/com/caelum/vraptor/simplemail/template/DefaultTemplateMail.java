package br.com.caelum.vraptor.simplemail.template;

import java.io.IOException;

import org.apache.commons.mail.SimpleEmail;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.freemarker.Freemarker;
import br.com.caelum.vraptor.freemarker.Template;
import br.com.caelum.vraptor.simplemail.AsyncMailer;

public class DefaultTemplateMail implements TemplateMail {

	private final Template template;
	private final Localization localization;
	private final AsyncMailer mailer;
	private final String templateName;
	private final Object[] nameParameters;

	public DefaultTemplateMail(String templateName, Freemarker freemarker, Localization localization, AsyncMailer mailer, Object... nameParameters) throws IOException {
		this.templateName = templateName;
		this.nameParameters = nameParameters;
		this.template = freemarker.use(templateName + ".ftl");
		this.localization = localization;
		this.mailer = mailer;
	}

	@Override
	public TemplateMail with(String key, Object value) {
		this.template.with(key, value);
		return this;
	}

	@Override
	public void dispatchTo(String name, String toMail) {
		with("to.name", name);
		with("to.email", toMail);
		with("signer", this.localization.getMessage("signer"));
		SimpleEmail email = new SimpleEmail();
		try {
			email.addTo(toMail, name);
			email.setSubject(this.localization.getMessage(this.templateName, nameParameters));
			email.setMsg(this.template.getContent());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.mailer.asyncSend(email);
	}

}
