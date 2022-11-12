package com.seabird.whatsdev.ui

interface ActivityListener {

    fun lockNavigationDrawer()
    fun unlockNavigationDrawer()
    fun onStatusItemSelected()
    fun hideAddGroupAction()
    fun showAddGroupAction()

}