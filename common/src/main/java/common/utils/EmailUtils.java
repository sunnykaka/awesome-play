package common.utils;

import common.utils.play.BaseGlobal;
import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;

/**
 * Created by zhb on 15-4-30.
 */
public class EmailUtils {


    public static boolean sendEmail(String mailTo,String title,String template) {

        try {
            play.Logger.debug(String.format("开始发送邮件, 收件人:%s, 标题:%s", mailTo, title));
            MailerClient mailerClient = BaseGlobal.injector.instanceOf(MailerClient.class);

            Email email = new Email();
            email.setFrom(Play.application().configuration().getString("play.mailer.from", "尚客<service@yezaoshu.com>"));
            email.addTo(mailTo);
            email.setSubject(title);
            email.setBodyHtml(template);

            mailerClient.send(email);

            play.Logger.debug(String.format("邮件发送完成, 收件人:%s, 标题:%s", mailTo, title));

            return true;
        } catch (Exception e) {
            play.Logger.error("发送邮件失败", e);
        }

        return false;

    }

}
