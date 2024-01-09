package com.example.myapplication
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.SplashFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                logoutUser()
            }
            // Add other cases for additional menu items if needed

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun logoutUser() {
        // Log out the user using FirebaseAuth
        FirebaseAuth.getInstance().signOut()

        // TODO: You may want to navigate the user to the login screen or perform other actions after logout

        // Example: If you are using Navigation component, navigate to the login fragment
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.splashFragment)
    }
}