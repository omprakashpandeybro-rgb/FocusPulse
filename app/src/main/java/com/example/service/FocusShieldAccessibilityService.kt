package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.db.FocusPulseDatabase
import com.example.ui.blocker.DistractionShieldActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FocusShieldAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var lastInterceptTimestamp = 0L
    private val debounceMillis = 1800L

    // In-memory cache of blocked packages
    private val blockedPackages = mutableMapOf<String, String>()
    private var isShortsBlockedOnly = true

    override fun onServiceConnected() {
        super.onServiceConnected()
        FocusSessionManager.setAccessibilityConnected(true)
        loadBlockedApps()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        FocusSessionManager.setAccessibilityConnected(false)
        return super.onUnbind(intent)
    }

    private fun loadBlockedApps() {
        scope.launch {
            try {
                val db = FocusPulseDatabase.getDatabase(applicationContext)
                val active = db.blockedAppDao().getActiveBlockedApps()
                synchronized(blockedPackages) {
                    blockedPackages.clear()
                    active.forEach { app ->
                        blockedPackages[app.packageName] = app.appName
                    }
                }
            } catch (_: Exception) {}
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!FocusSessionManager.isActive.value) return

        val pkg = event.packageName?.toString() ?: return
        if (pkg == applicationContext.packageName) return

        val now = SystemClock.elapsedRealtime()
        if (now - lastInterceptTimestamp < debounceMillis) return

        // 1. YouTube Specific Handling: Block Shorts, Allow Study/Longform videos
        if (pkg == "com.google.android.youtube") {
            if (FocusSessionManager.isShortsFilterEnabled.value) {
                val rootNode = rootInActiveWindow ?: return
                if (isYouTubeShortsActive(rootNode)) {
                    lastInterceptTimestamp = now
                    triggerShield(
                        packageName = pkg,
                        appName = "YouTube Shorts",
                        reason = "YouTube Shorts are disabled during focus sessions. Regular lectures and study videos are permitted!",
                        wasShorts = true
                    )
                }
            }
            return
        }

        // 2. Social Media & Gaming Apps Blocking
        val appName = synchronized(blockedPackages) { blockedPackages[pkg] }
        if (appName != null) {
            lastInterceptTimestamp = now
            triggerShield(
                packageName = pkg,
                appName = appName,
                reason = "$appName is locked until your deep work session timer expires.",
                wasShorts = false
            )
        }
    }

    /**
     * Inspects accessibility node hierarchy to detect if the user is in YouTube Shorts
     */
    private fun isYouTubeShortsActive(node: AccessibilityNodeInfo): Boolean {
        // Check current node
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""

        if (viewId.contains("reel") || viewId.contains("shorts") || viewId.contains("pivot_shorts")) {
            return true
        }
        if (desc.contains("shorts") && (desc.contains("player") || desc.contains("selected") || desc.contains("pivot"))) {
            return true
        }

        // Recursively inspect children with depth limit
        return inspectChildren(node, 0, 4)
    }

    private fun inspectChildren(node: AccessibilityNodeInfo, depth: Int, maxDepth: Int): Boolean {
        if (depth > maxDepth) return false
        val count = node.childCount
        for (i in 0 until count) {
            val child = node.getChild(i) ?: continue
            val viewId = child.viewIdResourceName?.lowercase() ?: ""
            val desc = child.contentDescription?.toString()?.lowercase() ?: ""
            val text = child.text?.toString()?.lowercase() ?: ""

            if (viewId.contains("reel_watch") || viewId.contains("shorts_container") ||
                viewId.contains("reel_recycler") || viewId.contains("shorts_player")
            ) {
                return true
            }
            if (desc == "shorts" && child.isSelected) {
                return true
            }
            if (inspectChildren(child, depth + 1, maxDepth)) {
                return true
            }
        }
        return false
    }

    private fun triggerShield(
        packageName: String,
        appName: String,
        reason: String,
        wasShorts: Boolean
    ) {
        // Record event in FocusSessionManager & Database
        FocusSessionManager.recordBlockedAttempt(
            context = applicationContext,
            packageName = packageName,
            appName = appName,
            wasShorts = wasShorts
        )

        // Launch the friendly DistractionShieldActivity overlay
        val intent = Intent(applicationContext, DistractionShieldActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(DistractionShieldActivity.EXTRA_APP_NAME, appName)
            putExtra(DistractionShieldActivity.EXTRA_REASON, reason)
            putExtra(DistractionShieldActivity.EXTRA_WAS_SHORTS, wasShorts)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {
        // Accessibility service interrupted
    }
}
