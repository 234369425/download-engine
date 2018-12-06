package com.beheresoft.download.component.download.http.entity

import io.netty.handler.codec.http.HttpHeaders

class HttpHeader : HttpHeaders() {

    private val headers = HashMap<String, String>()

    override fun contains(name: String?): Boolean {
        return headers.contains(name)
    }

    override fun clear(): HttpHeaders {
        headers.clear()
        return this
    }

    override fun names(): MutableSet<String> {
        return headers.keys
    }

    override fun getAll(name: String?): MutableList<String> {
        val value = headers.getOrDefault(name, "")
        return ArrayList(value.split(","))
    }

    override fun add(name: String?, value: Any?): HttpHeaders {
        if (value == null || name == null) {
            return this
        }
        headers[name] = value.toString()
        return this
    }

    override fun add(name: String?, values: MutableIterable<*>?): HttpHeaders {
        if (values == null || name == null) {
            return this
        }

        add(name, get(name) + values.joinToString())
        return this
    }

    override fun getTimeMillis(name: CharSequence?): Long {
        return getTimeMillis(name, -1)
    }

    override fun getTimeMillis(name: CharSequence?, defaultValue: Long): Long {
        val value = headers[name] ?: defaultValue
        return value as Long
    }

    override fun iterator(): MutableIterator<MutableMap.MutableEntry<String, String>> {
        return headers.entries.iterator()
    }

    override fun size(): Int {
        return headers.size
    }

    override fun get(name: String?): String {
        return headers.getOrDefault(name, "")
    }

    override fun iteratorCharSequence(): MutableIterator<MutableMap.MutableEntry<CharSequence, CharSequence>> {
        val map = HashMap<CharSequence, CharSequence>()
        headers.forEach {
            map[it.key] = it.value
        }
        return map.entries.iterator()
    }

    override fun entries(): MutableList<MutableMap.MutableEntry<String, String>> {
        return ArrayList(headers.entries)
    }

    override fun isEmpty(): Boolean {
        return headers.isEmpty()
    }

    override fun getInt(name: CharSequence?): Int {
        return getInt(name)
    }

    override fun getInt(name: CharSequence?, defaultValue: Int): Int {
        val value = headers[name] ?: defaultValue
        return value as Int
    }

    override fun addShort(name: CharSequence?, value: Short): HttpHeaders {
        return add(name, value)
    }

    override fun remove(name: String?): HttpHeaders {
        headers.remove(name)
        return this
    }

    override fun set(name: String?, value: Any?): HttpHeaders {
        if (name == null || value == null) {
            return this
        }
        headers[name] = value.toString()
        return this
    }

    override fun set(name: String?, values: MutableIterable<*>?): HttpHeaders {
        if (name == null || values == null) {
            return this
        }
        headers[name] = values.joinToString()
        return this
    }

    override fun setShort(name: CharSequence?, value: Short): HttpHeaders {
        return add(name, value)
    }

    override fun addInt(name: CharSequence?, value: Int): HttpHeaders {
        return add(name, value)
    }

    override fun getShort(name: CharSequence?): Short {
        return getShort(name, -1)
    }

    override fun getShort(name: CharSequence?, defaultValue: Short): Short {
        val value = headers[name] ?: defaultValue
        return value as Short
    }

    override fun setInt(name: CharSequence?, value: Int): HttpHeaders {
        return add(name, value)
    }

    override fun copy(): HttpHeader {
        val header = HttpHeader()
        headers.forEach {
            header[it.key] = it.value
        }
        return header
    }

}