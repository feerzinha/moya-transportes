package mtc.fernanda.moyatransportes

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.email_content.*
import java.util.*

class EmailFragment : Fragment() {

    val EMAIL_REQUEST_CODE = 1001

    lateinit var messageTypeArray: Array<String>

    lateinit var storeArray: Array<String>

    lateinit var storeCheckedArray: BooleanArray

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.email_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //call here some method

        setupUI()
    }

    private fun setupUI() {
        storeArray = resources.getStringArray(R.array.store_array)
        messageTypeArray = resources.getStringArray(R.array.email_type_array)

        ArrayAdapter.createFromResource(
            context,
            R.array.email_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_email.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            context,
            R.array.client_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_client.adapter = adapter
        }

        btn_enviar.setOnClickListener {
            sendEmail(prepareMessage())
        }

        spinner_email?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var itemSelected = spinner_email.getItemAtPosition(position).toString()
                if (itemSelected == messageTypeArray[2]) {
                    //occurence selected - Hide Route
                    etx_route.visibility = View.GONE
                    btn_add_store.visibility = View.GONE
                } else {
                    etx_route.visibility = View.VISIBLE
                    btn_add_store.visibility = View.VISIBLE
                }
            }

        }

        //Initialize store checked items with false
        storeCheckedArray = BooleanArray(storeArray.size) { i -> false }

        btn_add_store.setOnClickListener {
            show_store_list()
        }
    }

    private fun show_store_list() {
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this!!.context!!)

        builder.setTitle("Escolha as lojas")

        builder.setMultiChoiceItems(storeArray, storeCheckedArray) { dialog, which, isChecked ->
            // Update the clicked item checked status
            storeCheckedArray[which] = isChecked
        }

        builder.setPositiveButton("OK") { _, _ ->
            etx_route.text.clear()
            for (i in 0 until storeArray.size) {
                val checked = storeCheckedArray[i]
                if (checked) {
                    etx_route.setText("${etx_route.text}  ${storeArray[i]} \n")
                }
            }
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun prepareMessage(): String {
        val messageType = spinner_email.selectedItem.toString()
        val routeText = etx_route.text
        val obsText = etx_obs.text

        var emailMessage = ""

        if (messageType.equals(messageTypeArray[0])) {
            //This is message type start delivery
            emailMessage = getString(R.string.start_delivery_message).format(routeText)

            if (!obsText.isEmpty()) {
                emailMessage += getString(R.string.obs_message_text).format(obsText)
            }
        } else if (messageType.equals(messageTypeArray[1])) {
            //This is message type end delivery
            emailMessage = getString(R.string.end_delivery_message).format(routeText)

            if (!obsText.isEmpty()) {
                emailMessage += getString(R.string.obs_message_text).format(obsText)
            }
        } else if (messageType.equals(messageTypeArray[2])) {
            //This is message type occurrence in delivery
            var message = obsText
            emailMessage = getString(R.string.occurrence_message).format(message)
        }

        return emailMessage
    }

    private fun sendEmail(message: String) {
        val clientText = spinner_client.selectedItem.toString()
        val clientEmail = clientText.split("\\s".toRegex())[2]

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:$clientEmail") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Moya Transportes - Relatório")
        intent.putExtra(Intent.EXTRA_TEXT, message)

        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(intent, EMAIL_REQUEST_CODE)
        }
    }

    companion object {
        fun newInstance() = EmailFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // SEND_TO action do not return a result code.
        if (requestCode == EMAIL_REQUEST_CODE) {
            Snackbar.make(email_layout, "Email Enviado", Toast.LENGTH_SHORT).show()
            clearFields()
        }
    }

    private fun clearFields() {
        etx_route.text.clear()
        etx_obs.text.clear()
        Arrays.fill(storeCheckedArray, false)
    }
}
