package foodrecommender.system.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import foodrecommender.system.R;
import foodrecommender.system.classes.SearchFood;
import foodrecommender.system.adapters.SearchFoodAdapter;

public class SearchFragment extends Fragment {
    private View view;
    private RecyclerView foodRecyclerView;
    private TextInputEditText searchFoodEditText;
    private LinearProgressIndicator linearProgressIndicator;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search, container, false);
        foodRecyclerView = view.findViewById(R.id.search_results_recycler_view);
        searchFoodEditText = view.findViewById(R.id.food_search_bar_edit_text);
        linearProgressIndicator = view.findViewById(R.id.searchF_progress_indicator);

        getTextInputs();
        return view;
    }

    private void getTextInputs(){
        linearProgressIndicator.show();
        foodSearch();
        searchFoodEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (!input.isEmpty()) {
                    // Input consists only of spaces
                    foodSearch();
                }
            }
        });
    }

    private void foodSearch(){
        if (isAdded()) {
            // Make a POST request to the API endpoint
            // String url = "http://192.168.0.41:5000/search";
            String url = getString(R.string.search_url);
            JSONObject data = new JSONObject();

            try {
                data.put("food_search_input", searchFoodEditText.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        linearProgressIndicator.hide();
                        // Parse the JSON response
                        JSONArray searchFoodsArray = response.getJSONArray("search_food_result");
                        ArrayList<SearchFood> searchFoods = new ArrayList<>();

                        // Iterate through the JSON array and create SearchFood objects
                        for (int i = 0; i < searchFoodsArray.length(); i++) {
                            JSONObject searchFoodObject = searchFoodsArray.getJSONObject(i);
                            String shortDesc = searchFoodObject.getString("Shrt_Desc");
                            String kcal = searchFoodObject.getString("Energ_Kcal");
                            String foodGroup = searchFoodObject.getString("FoodGroup");
                            SearchFood searchFood = new SearchFood(shortDesc, foodGroup, kcal);
                            searchFoods.add(searchFood);
                        }

                        // Pass the searchFoods ArrayList to your RecyclerView adapter
                        SearchFoodAdapter adapter = new SearchFoodAdapter(searchFoods);
                        foodRecyclerView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    foodSearch();
                }
            });

            Volley.newRequestQueue(getActivity()).add(jsonObjectRequest);
        }
    }
}