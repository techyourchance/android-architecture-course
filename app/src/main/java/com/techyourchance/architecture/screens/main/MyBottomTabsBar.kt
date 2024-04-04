package com.techyourchance.architecture.screens.main

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.techyourchance.architecture.screens.BottomTab
import com.techyourchance.architecture.screens.Route


@Composable
fun MyBottomTabsBar(
    bottomTabs: List<BottomTab>,
    selectedBottomTab: BottomTab?,
    onBottomTabSelected: (BottomTab) -> Unit,
) {

    NavigationBar {
        bottomTabs.forEachIndexed { _, bottomTab ->
            NavigationBarItem(
                alwaysShowLabel = true,
                icon = { Icon(bottomTab.icon!!, contentDescription = bottomTab.title) },
                label = { Text(bottomTab.title) },
                selected = selectedBottomTab == bottomTab,
                onClick = { onBottomTabSelected(bottomTab) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}