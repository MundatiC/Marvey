package com.example.mpesa.stkpush.api.response

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * @author Fredrick Ochieng on 02/02/2018.
 */
class CallbackMetadata {
    @SerializedName("Item")
    @Expose
    var item: List<Item>? = null
}