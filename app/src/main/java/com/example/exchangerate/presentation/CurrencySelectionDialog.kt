package com.example.exchangerate.presentation

import android.content.Context
import com.example.exchangerate.domain.model.Currency
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CurrencySelectionDialog(
    context: Context,
    title: String,
    defaultSelectedItemIndex: Int,
    onSelectItem: (Currency) -> Unit
) {
    private val items = Currency.values()

    private val dialog = MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setNeutralButton("취소") { dialog, _ ->
            dialog.dismiss()
        }.setSingleChoiceItems(
            items.map { "$it(${it.currencyName})" }.toTypedArray(),
            defaultSelectedItemIndex
        ) { dialog, which ->
            onSelectItem(items[which])
            dialog.dismiss()
        }.create()

    fun show() = dialog.show()
}