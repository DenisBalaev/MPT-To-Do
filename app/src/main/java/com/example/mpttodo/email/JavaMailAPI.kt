@file:Suppress("DEPRECATION")

package com.example.mpttodo.email

import android.os.AsyncTask
import com.example.mpttodo.Constant
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


@Suppress("DEPRECATION")
class JavaMailAPI
    (
    private val mEmail: String,
    private val mSubject: String,
    private val mMessage: String
) :
    AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {
        val props = Properties()

        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = "smtp.gmail.com"
        props["mail.smtp.port"] = "587"

        val mSession = Session.getDefaultInstance(props,
            object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Constant().EMAIL, Constant().PASSWORD)
                }
            })
        try {
            val mm = MimeMessage(mSession)
            mm.setFrom(InternetAddress(Constant().EMAIL))
            mm.addRecipient(Message.RecipientType.TO, InternetAddress(mEmail))
            mm.subject = mSubject
            mm.setText(mMessage)
            Transport.send(mm)
        } catch (e: MessagingException) {
            e.printStackTrace()
        }
        return null
    }
}
