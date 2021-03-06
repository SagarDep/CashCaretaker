package com.androidessence.cashcaretaker.transfer

import android.app.DatePickerDialog
import android.app.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.androidessence.cashcaretaker.DatePickerFragment
import com.androidessence.cashcaretaker.DecimalDigitsInputFilter
import com.androidessence.cashcaretaker.R
import com.androidessence.cashcaretaker.account.Account
import com.androidessence.cashcaretaker.addtransaction.AddTransactionDialog
import com.androidessence.cashcaretaker.base.BaseDialogFragment
import com.androidessence.cashcaretaker.data.CCDatabase
import com.androidessence.cashcaretaker.data.CCRepository
import com.androidessence.cashcaretaker.databinding.DialogAddTransferBinding
import com.androidessence.cashcaretaker.views.SpinnerInputEditText
import com.androidessence.utility.asUIString
import java.util.*

/**
 * Dialog that allows a user to transfer money from one account to another.
 */
class AddTransferDialog : BaseDialogFragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var viewModel: AddTransferViewModel
    private lateinit var binding: DialogAddTransferBinding

    private lateinit var fromAccount: SpinnerInputEditText<Account>
    private lateinit var toAccount: SpinnerInputEditText<Account>

    private var selectedDate: Date = Date()
        set(value) {
            binding.transferDate.setText(value.asUIString())
            field = value
        }

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val database = CCDatabase.getInMemoryDatabase(context!!)
                val repository = CCRepository(database)

                @Suppress("UNCHECKED_CAST")
                return AddTransferViewModel(repository) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogAddTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setTitle(getString(R.string.add_transfer))

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddTransferViewModel::class.java)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fromAccount = view.findViewById(R.id.transferFromAccount)
        toAccount = view.findViewById(R.id.transferToAccount)

        binding.transferAmount.filters = arrayOf(DecimalDigitsInputFilter())

        selectedDate = Date()
        binding.transferDate.setOnClickListener { showDatePicker() }

        binding.submitButton.setOnClickListener {
            addTransfer(
                    fromAccount.selectedItem,
                    toAccount.selectedItem,
                    binding.transferAmount.text.toString(),
                    selectedDate
            )
        }

        viewModel.getAccounts()
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel.accounts.observe(this, androidx.lifecycle.Observer { accounts ->
            accounts?.let {
                fromAccount.items = it
                toAccount.items = it
            }
        })

        viewModel.fromAccountError.observe(this, androidx.lifecycle.Observer { errorRes ->
            errorRes?.let {
                fromAccount.error = getString(it)
            }
        })

        viewModel.toAccountError.observe(this, androidx.lifecycle.Observer { errorRes ->
            errorRes?.let {
                toAccount.error = getString(it)
            }
        })

        viewModel.amountError.observe(this, androidx.lifecycle.Observer { errorRes ->
            errorRes?.let {
                binding.transferAmount.error = getString(it)
            }
        })

        viewModel.transferInserted.subscribe { dismiss() }.addToComposite()
    }

    private fun addTransfer(fromAccount: Account?, toAccount: Account?, amount: String, date: Date) {
        viewModel.addTransfer(fromAccount, toAccount, amount, date)
    }


    private fun showDatePicker() {
        val datePickerFragment = DatePickerFragment.newInstance(selectedDate)
        datePickerFragment.setTargetFragment(this, AddTransferDialog.REQUEST_DATE)
        datePickerFragment.show(fragmentManager, AddTransactionDialog::class.java.simpleName)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDate = calendar.time
    }

    companion object {
        private const val REQUEST_DATE = 0
    }
}