// I minimized this to better understand

public class EmailForwarder extends GenericMailet {

    private static final String EmailForwarderAttr = "EmailForwarderAttr";       

    public void service(Mail mail) throws MessagingException {

        // Then loop through each address in the recipient list and try to map
        // it according to the alias table

        if (mail.getAttribute(EmailForwarderAttr) == null) {
            mail.setAttribute(EmailForwarderAttr, true);
        } else {
            return;
        }
		
        Collection<MailAddress> recipients = mail.getRecipients();
        Collection<MailAddress> recipientsToAdd = new Vector<MailAddress>();

        for (MailAddress recipient : recipients) {
            try {
                String userName = recipient.getLocalPart();

				// call the REST service
                String mailAddr = ForwardEmailProvider.getEmail(userName); 
                if(mailAddr==null) {
                    log("forward Email is null for user:"+userName);
                } else {
                    MailAddress target = new MailAddress(mailAddr);
                    log("from: " + recipient.asString() + " to: " + target.asString());
                    recipientsToAdd.add(target);
                }
            } catch (AddressException ex) {
                String exceptionBuffer = "There is an invalid alias from " + recipient;
                log(exceptionBuffer);
            }
        }
        recipients.addAll(recipientsToAdd);
    }

    @Override
    public String getMailetInfo() {
        return "Email Forwarder mailet";
    }

}
