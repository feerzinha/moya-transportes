package mtc.fernanda.moyatransportes

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.email_content.*
import android.content.Intent
import android.net.Uri


class EmailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.email_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //call here some method

        //val spinner: Spinner = context.findViewById(R.id.spinner)
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter.createFromResource(
//            context,
//            R.array.client_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            // Specify the layout to use when the list of choices appears
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner
//            spinner.adapter = adapter
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_enviar.setOnClickListener {
            testeExecute()
        }
    }

    private fun testeExecute(){
        //EmailService.test(context)

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:feerzinha@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TESTE")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Olá")
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

        startActivity(Intent.createChooser(emailIntent, "Chooser Title"))
    }

    companion object {
        fun newInstance() = EmailFragment()
    }

//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        // Return the fragment view/layout
//        var binding: FragmentEmailBinding = DataBindingUtil.inflate(inflater, R.layout.email_content, container, false)
//
//        return binding.root
//    }




}

