package com.indra.iscs.meetrefapp.components.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.indra.iscs.meetrefapp.R
import com.indra.iscs.meetrefapp.XmppClientManager
import com.indra.iscs.meetrefapp.components.utils.Constants

class PendingRequestsFragment : Fragment() {
    private lateinit var addUserButton: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_pending_requests, container, false)
        initViews(view)
        return view
    }
    private fun initViews(view: View)
    {
        addUserButton = view.findViewById(R.id.fab_add_contact)
        addUserButton.setOnClickListener {
            createDialog(requireContext())
        }
    }
    private fun createDialog(context: Context)
    {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.basic_dialog_add, null)
        val editContactNameText = dialogView.findViewById<EditText>(R.id.editTextName)
        editContactNameText.hint = context.getString(R.string.enter_user_name)

        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.add_user))
            .setView(dialogView)
            .setPositiveButton(context.getString(R.string.add)) { dialog, _ ->
                val contactName = editContactNameText.text.toString().trim()
                if (contactName.isNotEmpty()) {
                    XmppClientManager.getInstance().requestSubscription(contactName + Constants.JID_DOMAIN)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.group_name_cannot_be_empty), Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }.show()
    }
}