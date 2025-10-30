package com.example.recloopmart.ui

import android.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.recloopmart.R
import com.example.recloopmart.viewmodel.AiAssistantViewModel

object AiAssistantHelper {
    fun attach(activity: AppCompatActivity) {
        val root = activity.findViewById<View>(android.R.id.content) as FrameLayout
        val bubble = activity.layoutInflater.inflate(R.layout.include_ai_bubble, root, false)
        root.addView(bubble)

        val vm = ViewModelProvider(activity)[AiAssistantViewModel::class.java]

        val container = bubble.findViewById<View>(R.id.aiBubbleContainer)
        val iv = bubble.findViewById<View>(R.id.ivAiBubble)
        val menu = bubble.findViewById<View>(R.id.aiMenu)

        // FORCE VISIBILITY - Đảm bảo bubble luôn được hiển thị
        container.visibility = View.VISIBLE
        iv.visibility = View.VISIBLE
        iv.alpha = 1f
        
        // Set very high elevation to ensure bubble is always on top
        container.elevation = 100f
        container.translationZ = 100f
        iv.elevation = 101f  // HIGHER than container để đảm bảo hiển thị
        
        // Bring bubble to front to ensure clickability and visibility
        container.bringToFront()
        iv.bringToFront()
        
        // Đảm bảo bubble luôn visible sau mọi thay đổi
        root.post {
            container.bringToFront()
            container.elevation = 100f
            container.visibility = View.VISIBLE
            iv.visibility = View.VISIBLE
            iv.alpha = 1f
        }
        
        // Listener để khôi phục visibility mỗi khi layout thay đổi
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            container.bringToFront()
            container.elevation = 100f
            container.visibility = View.VISIBLE
            iv.visibility = View.VISIBLE
            iv.alpha = 1f
        }
        root.viewTreeObserver.addOnGlobalLayoutListener(listener)
        
        // Ensure bubble always stays on top
        val ensureOnTop = {
            container.bringToFront()
            container.elevation = 100f
            container.visibility = View.VISIBLE
            iv.visibility = View.VISIBLE
            iv.alpha = 1f
        }

        // Drag and tap handling - distinguish between tap and drag
        var isDragging = false
        var startX = 0f
        var startY = 0f
        var offsetX = 0f
        var offsetY = 0f
        
        iv.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    startY = event.rawY
                    offsetX = container.x - event.rawX
                    offsetY = container.y - event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = Math.abs(event.rawX - startX)
                    val dy = Math.abs(event.rawY - startY)
                    
                    if (dx > 10 || dy > 10) {
                        isDragging = true
                        container.x = event.rawX + offsetX
                        container.y = event.rawY + offsetY
                        
                        // Keep within bounds
                        if (container.x < 0) container.x = 0f
                        if (container.y < 0) container.y = 0f
                        if (container.x + container.width > root.width) {
                            container.x = root.width - container.width.toFloat()
                        }
                        if (container.y + container.height > root.height) {
                            container.y = root.height - container.height.toFloat()
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        // It's a tap - toggle menu
                        vm.toggleMenu()
                    } else {
                        // Snap to nearest edge after drag
                        val centerX = container.x + container.width / 2
                        val parentCenterX = root.width / 2
                        
                        container.x = if (centerX < parentCenterX) {
                            16f
                        } else {
                            root.width - container.width - 16f
                        }
                        
                        // Ensure vertical bounds
                        if (container.y < 16) container.y = 16f
                        if (container.y > root.height - container.height - 100) {
                            container.y = root.height - container.height - 100f
                        }
                    }
                    
                    // FORCE VISIBILITY after interaction
                    container.visibility = View.VISIBLE
                    iv.visibility = View.VISIBLE
                    iv.alpha = 1f
                    ensureOnTop()
                    true
                }
                else -> false
            }
        }

        // Observe menu visibility changes
        // Cách 1: Luôn bringToFront() sau khi menu thay đổi
        vm.menuVisible.observe(activity as LifecycleOwner) { visible ->
            menu.visibility = if (visible) View.VISIBLE else View.GONE
            container.bringToFront() // luôn luôn
            container.elevation = 100f
            container.visibility = View.VISIBLE  // FORCE VISIBLE
            iv.visibility = View.VISIBLE  // FORCE VISIBLE
            iv.alpha = 1f  // FORCE FULL OPACITY
            menu.bringToFront()
            root.invalidate()
        }

        // menu items click handlers
        bubble.findViewById<View>(R.id.btnCompare).setOnClickListener { 
            vm.toggleMenu()
            showCompareProductsDialog(activity)
        }
        bubble.findViewById<View>(R.id.btnAnalyze).setOnClickListener { 
            vm.toggleMenu()
            showAiAnalyzeDialog(activity)
        }
        bubble.findViewById<View>(R.id.btnChat).setOnClickListener { 
            vm.toggleMenu()
            showAiChatDialog(activity)
        }
    }

    private fun showCompareProductsDialog(activity: AppCompatActivity) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_compare_products, null)
        val etProductId1 = view.findViewById<EditText>(R.id.etProductId1)
        val etProductId2 = view.findViewById<EditText>(R.id.etProductId2)
        val btnCompare = view.findViewById<View>(R.id.btnCompareAction)
        val btnCancel = view.findViewById<View>(R.id.btnCancel)
        
        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()
        
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnCompare.setOnClickListener {
            val id1 = etProductId1.text.toString().trim()
            val id2 = etProductId2.text.toString().trim()
            
            if (TextUtils.isEmpty(id1) || TextUtils.isEmpty(id2)) {
                Toast.makeText(activity, "Vui lòng nhập đủ 2 ID sản phẩm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            dialog.dismiss()
            Toast.makeText(activity, "So sánh sản phẩm ID: $id1 vs $id2", Toast.LENGTH_SHORT).show()
        }
        
        dialog.show()
    }

    private fun showAiAnalyzeDialog(activity: AppCompatActivity) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_ai_analyze, null)
        val etProductInput = view.findViewById<EditText>(R.id.etProductInput)
        val btnAnalyze = view.findViewById<View>(R.id.btnAnalyzeAction)
        val btnCancel = view.findViewById<View>(R.id.btnCancel)
        
        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()
        
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnAnalyze.setOnClickListener {
            val input = etProductInput.text.toString().trim()
            
            if (TextUtils.isEmpty(input)) {
                Toast.makeText(activity, "Vui lòng nhập ID hoặc mô tả sản phẩm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            dialog.dismiss()
            Toast.makeText(activity, "Phân tích sản phẩm: $input", Toast.LENGTH_SHORT).show()
        }
        
        dialog.show()
    }

    private fun showAiChatDialog(activity: AppCompatActivity) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_ai_chat, null)
        val etMessageInput = view.findViewById<EditText>(R.id.etMessageInput)
        val btnSend = view.findViewById<View>(R.id.btnSend)
        val btnClose = view.findViewById<View>(R.id.btnClose)
        
        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()
        
        btnClose.setOnClickListener { dialog.dismiss() }
        btnSend.setOnClickListener {
            val message = etMessageInput.text.toString().trim()
            
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(activity, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            etMessageInput.setText("")
            Toast.makeText(activity, "Đã gửi: $message", Toast.LENGTH_SHORT).show()
        }
        
        dialog.show()
    }
}
