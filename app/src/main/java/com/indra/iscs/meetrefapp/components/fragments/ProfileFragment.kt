package com.indra.iscs.meetrefapp.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.indra.iscs.meetrefapp.R
import com.indra.iscs.meetrefapp.XmppClientManager

class ProfileFragment : Fragment() {

    private lateinit var imageViewProfile: ImageView
    private lateinit var textViewJid: TextView
    private lateinit var textViewUsername: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_profile_layout, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        imageViewProfile = view.findViewById(R.id.imageView_profile)
        textViewJid = view.findViewById(R.id.textView_jid)
        textViewUsername = view.findViewById(R.id.textView_username)
        textViewJid.text = XmppClientManager.getInstance().getUserJid()
        textViewUsername.text = XmppClientManager.getInstance().getUsername()
    }

}