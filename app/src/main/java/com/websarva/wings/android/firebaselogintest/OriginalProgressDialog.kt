package com.websarva.wings.android.firebaselogintest

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.websarva.wings.android.firebaselogintest.databinding.ProgressDialogBinding

class OriginalProgressDialog : DialogFragment() {

    private lateinit var mBinding: ProgressDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(context!!)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.progress_dialog, null, false)
        arguments?.getInt(RES_STRING_ID)?.let {
            mBinding.progressMessage.text = getText(it)
        }
        builder.setView(mBinding.root)

        return builder.create()
    }

    fun setMessage(@StringRes id: Int) {
        arguments?.putInt(RES_STRING_ID, id)
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    companion object {
        const val RES_STRING_ID = "RES_STRING_ID"

        fun newInstance(@StringRes id: Int): OriginalProgressDialog {
            val f = OriginalProgressDialog()

            val args = Bundle()
            args.putInt(RES_STRING_ID, id)
            f.arguments = args

            return f
        }
    }
}