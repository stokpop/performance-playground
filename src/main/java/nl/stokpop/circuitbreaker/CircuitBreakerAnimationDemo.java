package nl.stokpop.circuitbreaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CircuitBreakerAnimationDemo extends JPanel implements ActionListener {
    private Timer timer;
    private PaymentServiceWithCircuitBreaker service;
    private int currentPhase = 0; // 0=Phase1, 1=Phase2, 2=Phase3
    private int currentCall = 0;
    private long phaseStartTime;
    private List<CallResult> callHistory = new ArrayList<>();
    private boolean animationComplete = false;
    private MetricsSnapshot latestMetrics = null; // Store the latest metrics
    
    // Animation state
    private int animationTime = 0;
    private static final int TOTAL_ANIMATION_TIME = 30000; // 30 seconds
    
    // Colors
    private static final Color CLOSED_COLOR = new Color(34, 139, 34);    // Forest Green
    private static final Color OPEN_COLOR = new Color(220, 20, 60);      // Crimson
    private static final Color HALF_OPEN_COLOR = new Color(255, 165, 0); // Orange
    private static final Color SUCCESS_COLOR = new Color(46, 125, 50);   // Dark Green
    private static final Color FAILURE_COLOR = new Color(183, 28, 28);   // Dark Red
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    
    private static class CallResult {
        boolean success;
        long timestamp;
        String phase;
        
        CallResult(boolean success, String phase) {
            this.success = success;
            this.timestamp = System.currentTimeMillis();
            this.phase = phase;
        }
    }

    private static class MetricsSnapshot {
        private final String state;
        private final float failureRate;
        private final int totalCalls;
        private final int failedCalls;
        private final int successfulCalls;
        private final int slowCalls;
        private final long timestamp;

        public MetricsSnapshot(String state, float failureRate, int totalCalls, 
                int failedCalls, int successfulCalls, int slowCalls) {
            this.state = state;
            this.failureRate = failureRate;
            this.totalCalls = totalCalls;
            this.failedCalls = failedCalls;
            this.successfulCalls = successfulCalls;
            this.slowCalls = slowCalls;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public CircuitBreakerAnimationDemo() {
        service = new PaymentServiceWithCircuitBreaker();
        timer = new Timer(50, this); // 20 FPS
        phaseStartTime = System.currentTimeMillis();
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(1000, 700));

        // Initialize metrics
        captureLatestMetrics();
    }
    
    public void startAnimation() {
        timer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (animationComplete) return;
        
        animationTime += 50;
        long currentTime = System.currentTimeMillis();
        long phaseElapsed = currentTime - phaseStartTime;
        
        // Phase transitions based on timing
        if (currentPhase == 0 && phaseElapsed > 8000) { // Phase 1: 8 seconds
            currentPhase = 1;
            phaseStartTime = currentTime;
            currentCall = 0;
        } else if (currentPhase == 1 && phaseElapsed > 6000) { // Phase 2: 6 seconds
            currentPhase = 2;
            phaseStartTime = currentTime;
            currentCall = 0;
            service.setServiceHealthy(true);
        } else if (currentPhase == 2 && phaseElapsed > 16000) { // Phase 3: 16 seconds
            animationComplete = true;
        }
        
        // Make calls at appropriate intervals
        makeCallIfNeeded(phaseElapsed);
        
        repaint();
        
        if (animationTime >= TOTAL_ANIMATION_TIME) {
            timer.stop();
            animationComplete = true;
        }
    }
    
    private void makeCallIfNeeded(long phaseElapsed) {
        int callInterval;
        if (currentPhase == 0) {
            callInterval = 700;  // Phase 1: call every 700ms
        } else if (currentPhase == 1) {
            callInterval = 0;    // Phase 2: no calls (waiting)
        } else if (currentPhase == 2) {
            callInterval = 1000; // Phase 3: call every 1000ms
        } else {
            callInterval = 1000;
        }
        
        if (callInterval > 0 && phaseElapsed > currentCall * callInterval) {
            makeCall();
            currentCall++;
        }
    }
    
    private void makeCall() {
        String callId;
        if (currentPhase == 0) {
            callId = "PAY-" + (currentCall + 1);
        } else if (currentPhase == 1) {
            callId = "PAY-" + (100 + currentCall); // Use different range for waiting phase
        } else if (currentPhase == 2) {
            callId = "PAY-" + (currentCall + 21);
        } else {
            callId = "";
        }

        if (!callId.isEmpty()) {
            PaymentRequest request = new PaymentRequest(
                callId,
                new BigDecimal(currentPhase == 0 ? "99.99" : (currentPhase == 1 ? "75.00" : "50.00")),
                "CUSTOMER-" + (currentPhase == 1 ? "W" + currentCall : currentCall)
            );

            PaymentResult result = service.processPaymentSafe(request);
            boolean success = "SUCCESS".equals(result.getStatus());
            String phase;
            if (currentPhase == 0) {
                phase = "Triggering";
            } else if (currentPhase == 1) {
                phase = "Waiting";
            } else {
                phase = "Recovery";
            }

            callHistory.add(new CallResult(success, phase));

            // Keep only last 20 calls for display
            if (callHistory.size() > 20) {
                callHistory.remove(0);
            }

            // Capture metrics after each call for immediate update
            captureLatestMetrics();
        }
    }

    private void captureLatestMetrics() {
        var cb = service.getCircuitBreaker();
        var metrics = cb.getMetrics();

        latestMetrics = new MetricsSnapshot(
            cb.getState().name(),
            metrics.getFailureRate(),
            metrics.getNumberOfFailedCalls() + metrics.getNumberOfSuccessfulCalls(),
            metrics.getNumberOfFailedCalls(),
            metrics.getNumberOfSuccessfulCalls(),
            metrics.getNumberOfSlowCalls()
        );
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawTitle(g2d);
        drawCircuitBreakerState(g2d);
        drawPhaseIndicator(g2d);
        drawMetrics(g2d);
        drawCallHistory(g2d);
        drawProgressBar(g2d);
        drawLegend(g2d);
    }
    
    private void drawTitle(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "Circuit Breaker Pattern Animation";
        int x = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, x, 30);
    }
    
    private void drawCircuitBreakerState(Graphics2D g2d) {
        var cb = service.getCircuitBreaker();
        var state = cb.getState();
        
        // Circuit breaker visual representation
        int centerX = getWidth() / 2;
        int centerY = 120;
        int radius = 60;
        
        // Draw circuit
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(Color.BLACK);
        g2d.drawLine(centerX - radius - 20, centerY, centerX - radius, centerY);
        g2d.drawLine(centerX + radius, centerY, centerX + radius + 20, centerY);
        
        // Draw breaker switch based on state
        Color stateColor;
        String stateName = state.name();
        if ("CLOSED".equals(stateName)) {
            stateColor = CLOSED_COLOR;
        } else if ("OPEN".equals(stateName)) {
            stateColor = OPEN_COLOR;
        } else if ("HALF_OPEN".equals(stateName)) {
            stateColor = HALF_OPEN_COLOR;
        } else {
            stateColor = Color.GRAY;
        }
        
        g2d.setColor(stateColor);
        g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        
        // Draw switch position
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(6));
        if ("OPEN".equals(stateName)) {
            // Open switch - angled line
            g2d.drawLine(centerX - 20, centerY, centerX + 10, centerY - 25);
        } else {
            // Closed switch - horizontal line
            g2d.drawLine(centerX - 20, centerY, centerX + 20, centerY);
        }
        
        // State label
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String stateText = stateName;
        int textX = centerX - fm.stringWidth(stateText) / 2;
        g2d.drawString(stateText, textX, centerY + radius + 25);
    }
    
    private void drawPhaseIndicator(Graphics2D g2d) {
        String[] phases = {"🔥 Phase 1: Triggering Failures", "⏰ Phase 2: Waiting for Recovery", "🔄 Phase 3: Testing Recovery"};
        String currentPhaseText;
        if (currentPhase < phases.length) {
            currentPhaseText = phases[currentPhase];
        } else {
            currentPhaseText = "✅ Complete";
        }
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(currentPhaseText)) / 2;
        g2d.drawString(currentPhaseText, x, 220);
    }
    
    private void drawMetrics(Graphics2D g2d) {
        // If we don't have metrics yet, capture them
        if (latestMetrics == null) {
            captureLatestMetrics();
        }

        int startX = 50;
        int startY = 260;

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));

        // Metrics box
        g2d.setColor(new Color(250, 250, 250));
        g2d.fillRoundRect(startX - 10, startY - 20, 300, 160, 10, 10);
        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(startX - 10, startY - 20, 300, 160, 10, 10);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Circuit Breaker Metrics", startX, startY);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        int lineHeight = 20;
        int currentY = startY + 25;

        // Failure rate
        float failureRate = latestMetrics.failureRate;
        String failureRateText;
        if (failureRate < 0) {
            failureRateText = "Failure Rate: N/A (need 5+ calls)";
        } else {
            failureRateText = String.format("Failure Rate: %.1f%%", failureRate);
        }
        g2d.drawString(failureRateText, startX, currentY);
        currentY += lineHeight;

        // Call counts
        g2d.drawString(String.format("Total Calls: %d", latestMetrics.totalCalls), startX, currentY);
        currentY += lineHeight;

        g2d.drawString(String.format("Failed Calls: %d", latestMetrics.failedCalls), startX, currentY);
        currentY += lineHeight;

        g2d.drawString(String.format("Successful Calls: %d", latestMetrics.successfulCalls), startX, currentY);
        currentY += lineHeight;

        g2d.drawString(String.format("Slow Calls: %d", latestMetrics.slowCalls), startX, currentY);

        // Add time of last update
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.setColor(Color.DARK_GRAY);
        String updateText = "Updated after last call";
        g2d.drawString(updateText, startX, startY + 125);
    }
    
    private void drawCallHistory(Graphics2D g2d) {
        if (callHistory.isEmpty()) return;
        
        int startX = 400;
        int startY = 260;
        int boxWidth = 540;
        int boxHeight = 160;
        
        // History box
        g2d.setColor(new Color(250, 250, 250));
        g2d.fillRoundRect(startX - 10, startY - 20, boxWidth, boxHeight, 10, 10);
        g2d.setColor(Color.GRAY);
        g2d.drawRoundRect(startX - 10, startY - 20, boxWidth, boxHeight, 10, 10);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Recent Calls", startX, startY);
        
        // Draw call indicators
        int callSize = 15;
        int spacing = 25;
        int maxCallsPerRow = (boxWidth - 20) / spacing;
        
        for (int i = 0; i < callHistory.size(); i++) {
            CallResult call = callHistory.get(i);
            int row = i / maxCallsPerRow;
            int col = i % maxCallsPerRow;
            
            int x = startX + col * spacing;
            int y = startY + 30 + row * spacing;
            
            g2d.setColor(call.success ? SUCCESS_COLOR : FAILURE_COLOR);
            g2d.fillOval(x, y, callSize, callSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, callSize, callSize);
        }
    }
    
    private void drawProgressBar(Graphics2D g2d) {
        int barWidth = 800;
        int barHeight = 20;
        int barX = (getWidth() - barWidth) / 2;
        int barY = 450;
        
        // Progress bar background
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
        
        // Progress bar fill
        int progress = Math.min(animationTime * barWidth / TOTAL_ANIMATION_TIME, barWidth);
        g2d.setColor(new Color(76, 175, 80));
        g2d.fillRoundRect(barX, barY, progress, barHeight, 10, 10);
        
        // Progress text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String progressText = String.format("Animation Progress: %d%%", animationTime * 100 / TOTAL_ANIMATION_TIME);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = barX + (barWidth - fm.stringWidth(progressText)) / 2;
        g2d.drawString(progressText, textX, barY + barHeight + 15);
    }
    
    private void drawLegend(Graphics2D g2d) {
        int legendX = 50;
        int legendY = 500;
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Legend:", legendX, legendY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        int lineHeight = 18;
        int currentY = legendY + 20;
        
        // State colors
        drawLegendItem(g2d, CLOSED_COLOR, "CLOSED - Circuit allows calls", legendX, currentY);
        currentY += lineHeight;
        drawLegendItem(g2d, OPEN_COLOR, "OPEN - Circuit blocks calls", legendX, currentY);
        currentY += lineHeight;
        drawLegendItem(g2d, HALF_OPEN_COLOR, "HALF_OPEN - Testing recovery", legendX, currentY);
        currentY += lineHeight;
        
        // Call result colors
        drawLegendItem(g2d, SUCCESS_COLOR, "Successful call", legendX + 300, legendY + 20);
        drawLegendItem(g2d, FAILURE_COLOR, "Failed call", legendX + 300, legendY + 38);
    }
    
    private void drawLegendItem(Graphics2D g2d, Color color, String text, int x, int y) {
        g2d.setColor(color);
        g2d.fillOval(x, y - 8, 12, 12);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y - 8, 12, 12);
        g2d.drawString(text, x + 20, y);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Circuit Breaker Animation Demo");
                CircuitBreakerAnimationDemo demo = new CircuitBreakerAnimationDemo();
                
                frame.add(demo);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
                demo.startAnimation();
            }
        });
    }
}
