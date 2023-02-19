package com.vitorpamplona.amethyst.ui.dal

import com.vitorpamplona.amethyst.model.LocalCache
import com.vitorpamplona.amethyst.model.Note
import com.vitorpamplona.amethyst.model.User
import com.vitorpamplona.amethyst.service.model.LnZapEvent

object UserProfileReportsFeedFilter: FeedFilter<Note>() {
  var user: User? = null

  fun loadUserProfile(userId: String) {
    user = LocalCache.getOrCreateUser(userId)
  }

  override fun feed(): List<Note> {
    return user?.reports?.values?.flatten()?.sortedBy { it.event?.createdAt }?.reversed() ?: emptyList()
  }
}