package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.foreverrafs.superdiary.ui.navigation.BottomNavigationRoute
import com.foreverrafs.superdiary.ui.navigation.SuperDiaryTab

@Composable
fun SuperDiaryBottomBar(navController: NavController) {
    val items = listOf(
        BottomNavigationRoute.DashboardTab,
        BottomNavigationRoute.FavoriteTab,
        BottomNavigationRoute.DiaryChatTab,
    )

    val currentBackStackEntry = navController.currentBackStackEntryAsState().value

    NavigationBar {
        items.forEach { tab ->
            val selected = currentBackStackEntry?.destination?.route == tab::class.qualifiedName
            BottomNavigationItem(
                tab = tab,
                selected = selected,
            ) {
                navController.navigate(tab) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavigationItem(
    tab: SuperDiaryTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        modifier = Modifier.testTag(tab.options.title),
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                painter = if (selected) {
                    tab.selectedIcon
                } else {
                    tab.options.icon
                        ?: rememberVectorPainter(Icons.Default.Home)
                },
                contentDescription = tab.options.title,
            )
        },
        label = {
            Text(
                text = tab.options.title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        },
    )
}
