package com.example.farmers.ui.agent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.ChatMessage;
import com.example.farmers.data.model.GeminiRequest;
import com.example.farmers.data.model.GeminiResponse;
import com.example.farmers.databinding.FragmentAgentBinding;
import com.example.farmers.ui.adapter.ChatAdapter;
import com.example.farmers.util.PrefsManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgentFragment extends Fragment {

    private FragmentAgentBinding binding;
    private ChatAdapter chatAdapter;
    private PrefsManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAgentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PrefsManager.getInstance(requireContext());

        setupChat();
        setupSuggestionChips();

        binding.btnSendMessage.setOnClickListener(v -> sendMessage());
        binding.btnClearChat.setOnClickListener(v -> {
            setupChat();
            android.widget.Toast.makeText(requireContext(), "Chat history reset", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void setupChat() {
        chatAdapter = new ChatAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(layoutManager);
        binding.rvChat.setAdapter(chatAdapter);

        // Pre-seed Welcome message
        chatAdapter.addMessage(new ChatMessage(
                "🌾 Welcome! I am AgriSmart, your agronomic AI advisor.\n\n" +
                "I analyze your live farm climate, soil moisture, and weather forecast in " +
                prefs.getLocationName() + " to guide irrigation, crop health, pest prevention, and garden care.\n\n" +
                "How can I help your farm or garden today?",
                ChatMessage.TYPE_BOT
        ));

        String key = prefs.getGeminiApiKey();
        if (key != null && !key.trim().isEmpty()) {
            binding.tvApiKeyStatus.setText("GEMINI AI ACTIVE");
        } else {
            binding.tvApiKeyStatus.setText("AGRI-RULE ENGINE");
        }
    }

    private void setupSuggestionChips() {
        binding.chipIrrigate.setOnClickListener(v -> sendQuery("Based on current soil moisture and rain forecast, when should I irrigate?"));
        binding.chipSpray.setOnClickListener(v -> sendQuery("Is it safe to spray pesticides or foliar fertilizers in this wind?"));
        binding.chipPests.setOnClickListener(v -> sendQuery("What fungal or pest risks should I watch out for in this humidity?"));
        binding.chipFertilizer.setOnClickListener(v -> sendQuery("What are the best fertilizer application practices right now?"));
    }

    private void sendQuery(String query) {
        binding.etChatMessage.setText(query);
        sendMessage();
    }

    private void sendMessage() {
        String query = binding.etChatMessage.getText() != null ? binding.etChatMessage.getText().toString().trim() : "";
        if (query.isEmpty()) return;

        binding.etChatMessage.setText("");
        chatAdapter.addMessage(new ChatMessage(query, ChatMessage.TYPE_USER));
        binding.rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        binding.pbAgentTyping.setVisibility(View.VISIBLE);

        String apiKey = prefs.getGeminiApiKey();
        if (apiKey != null && !apiKey.trim().isEmpty()) {
            callGeminiApi(query, apiKey);
        } else {
            // Intelligent agronomic offline rule engine fallback
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isAdded()) return;
                binding.pbAgentTyping.setVisibility(View.GONE);
                String response = getLocalAgronomicAdvice(query);
                chatAdapter.addMessage(new ChatMessage(response, ChatMessage.TYPE_BOT));
                binding.rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }, 800);
        }
    }

    private void callGeminiApi(String userQuery, String apiKey) {
        String systemInstruction = "You are an expert agricultural scientist, agronomist, and horticulture consultant called AgriSmart. " +
                "You provide practical, scientifically-grounded advice for farmers and gardeners. " +
                "Current Farm Context: Location=" + prefs.getLocationName() +
                ", Rain threshold=" + prefs.getRainThreshold() + "mm, Wind threshold=" + prefs.getWindThreshold() + "km/h. " +
                "Keep your answers structured, encouraging, and focused on crop yield, soil health, and water conservation.";

        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(new GeminiRequest.Content("user", userQuery));

        GeminiRequest request = new GeminiRequest(systemInstruction, contents);

        RetrofitClient.getGeminiService().generateContent("gemini-1.5-flash", apiKey, request)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                        if (!isAdded()) return;
                        binding.pbAgentTyping.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            String answer = response.body().getText();
                            chatAdapter.addMessage(new ChatMessage(answer, ChatMessage.TYPE_BOT));
                        } else {
                            // Fallback to local advisor if API returned error
                            String fallback = getLocalAgronomicAdvice(userQuery);
                            chatAdapter.addMessage(new ChatMessage(fallback, ChatMessage.TYPE_BOT));
                        }
                        binding.rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        binding.pbAgentTyping.setVisibility(View.GONE);
                        String fallback = getLocalAgronomicAdvice(userQuery);
                        chatAdapter.addMessage(new ChatMessage(fallback, ChatMessage.TYPE_BOT));
                        binding.rvChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                });
    }

    private String getLocalAgronomicAdvice(String query) {
        String q = query.toLowerCase();

        if (q.contains("irrigate") || q.contains("water") || q.contains("watering")) {
            return "💧 Irrigation Advisory:\n\n" +
                    "• Check Root Zone Depth: Inspect top 7–15 cm of soil. If moisture feels damp and clusters together, irrigation can wait.\n" +
                    "• Timing: Always irrigate early in the morning (05:00 - 08:30 AM) or after sunset to minimize evapotranspiration losses.\n" +
                    "• In Case of Rain: If upcoming 48-hour rain probability exceeds 50%, pause scheduled irrigation to avoid waterlogging and root rot.";
        } else if (q.contains("spray") || q.contains("chemical") || q.contains("pesticide") || q.contains("wind")) {
            return "💨 Spraying & Chemical Application Guidelines:\n\n" +
                    "• Wind Limit: Ideal spraying wind speed is 5–15 km/h. Avoid spraying if wind exceeds 20 km/h due to chemical drift onto adjacent fields.\n" +
                    "• Rain Window: Ensure no heavy precipitation is expected within 3–4 hours after application so chemicals are absorbed without being washed off.\n" +
                    "• Temperature: Do not apply herbicides when midday temperatures surpass 32°C to prevent crop phototoxicity.";
        } else if (q.contains("pest") || q.contains("disease") || q.contains("fungal") || q.contains("humid")) {
            return "🐛 Integrated Pest & Disease Management:\n\n" +
                    "• High Humidity Alert: Sustained humidity above 80% with warm temperatures encourages fungal blast, blight, and powdery mildew.\n" +
                    "• Prevention: Maintain proper crop spacing to ensure good airflow between rows. Use neem-oil spray as an organic preventive measure.\n" +
                    "• Inspection: Monitor leaf undersides and stems daily for aphid or whitefly colonies.";
        } else if (q.contains("fertilizer") || q.contains("nutrient") || q.contains("urea") || q.contains("npk")) {
            return "🌱 Fertilizer Application Best Practices:\n\n" +
                    "• Moisture Dependency: Apply chemical fertilizers only when soil has adequate moisture; applying onto bone-dry soil causes root burn.\n" +
                    "• Avoid Pre-Storm Feeding: Do not apply nitrogen/urea immediately before torrential rains to prevent nutrient leaching into groundwater.\n" +
                    "• Foliar Feeding: Best performed in early evening when stomata are receptive and UV index is low.";
        } else if (q.contains("sow") || q.contains("plant") || q.contains("seed")) {
            return "🌾 Sowing & Planting Conditions:\n\n" +
                    "• Soil Temp: Optimal germination for most field crops occurs between 20°C and 28°C.\n" +
                    "• Seedbed Prep: Ensure loose tilth and 0–7 cm surface moisture for rapid radicle emergence.\n" +
                    "• Check Weather: Avoid sowing if intense downpours are forecasted within 48 hours to prevent seed displacement.";
        } else {
            return "🌾 AgriSmart Recommendation for " + prefs.getLocationName() + ":\n\n" +
                    "Based on current regional weather patterns, prioritize monitoring root-zone moisture, maintain weed-free crop borders, and keep drainage furrows unblocked.\n\n" +
                    "Tip: You can add your Gemini API key in Settings for fully customized conversational reasoning!";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
