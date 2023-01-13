package com.vitorpamplona.amethyst.model

import androidx.lifecycle.LiveData
import com.vitorpamplona.amethyst.service.NostrSingleEventDataSource
import com.vitorpamplona.amethyst.ui.note.toDisplayHex
import fr.acinq.secp256k1.Hex
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Collections
import nostr.postr.events.Event

class Note(val idHex: String) {
    val id = Hex.decode(idHex)
    val idDisplayHex = id.toDisplayHex()

    var event: Event? = null
    var author: User? = null
    var mentions: List<User>? = null
    var replyTo: MutableList<Note>? = null

    val replies = Collections.synchronizedSet(mutableSetOf<Note>())
    val reactions = Collections.synchronizedSet(mutableSetOf<Note>())
    val boosts = Collections.synchronizedSet(mutableSetOf<Note>())

    fun loadEvent(event: Event, author: User, mentions: List<User>, replyTo: MutableList<Note>) {
        this.event = event
        this.author = author
        this.mentions = mentions
        this.replyTo = replyTo

        refreshObservers()
    }

    fun formattedDateTime(timestamp: Long): String {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("uuuu-MM-dd-HH:mm:ss"))
    }

    fun replyLevelSignature(): String {
        val replyTo = replyTo
        if (replyTo == null || replyTo.isEmpty()) {
            return "/" + formattedDateTime(event?.createdAt ?: 0) + ";"
        }

        return replyTo
            .map { it.replyLevelSignature() }
            .maxBy { it.length }.removeSuffix(";") + "/" + formattedDateTime(event?.createdAt ?: 0) + ";"
    }

    fun replyLevel(): Int {
        val replyTo = replyTo
        if (replyTo == null || replyTo.isEmpty()) {
            return 0
        }

        return replyTo.maxOf {
            it.replyLevel()
        } + 1
    }

    fun addReply(note: Note) {
        if (replies.add(note))
            refreshObservers()
    }

    fun addBoost(note: Note) {
        if (boosts.add(note))
            refreshObservers()
    }

    fun addReaction(note: Note) {
        if (reactions.add(note))
            refreshObservers()
    }

    fun isReactedBy(user: User): Boolean {
        return reactions.any { it.author == user }
    }

    fun isBoostedBy(user: User): Boolean {
        return boosts.any { it.author == user }
    }

    // Observers line up here.
    val live: NoteLiveData = NoteLiveData(this)

    private fun refreshObservers() {
        live.refresh()
    }
}

class NoteLiveData(val note: Note): LiveData<NoteState>(NoteState(note)) {
    fun refresh() {
        postValue(NoteState(note))
    }

    override fun onActive() {
        super.onActive()
        NostrSingleEventDataSource.add(note.idHex)
    }

    override fun onInactive() {
        super.onInactive()
        NostrSingleEventDataSource.remove(note.idHex)
    }
}

class NoteState(val note: Note)