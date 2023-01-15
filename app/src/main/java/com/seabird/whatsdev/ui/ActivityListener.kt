package com.seabird.whatsdev.ui

interface ActivityListener {

    fun showSearchGroup()
    fun hideSearchGroup()
    fun lockNavigationDrawer()
    fun unlockNavigationDrawer()
    fun onStatusItemSelected()
    fun hideAddGroupAction()
    fun showAddGroupAction()

    fun onFavouriteGroupSelected()

}