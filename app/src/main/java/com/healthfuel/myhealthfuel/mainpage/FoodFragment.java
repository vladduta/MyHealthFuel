package com.healthfuel.myhealthfuel.mainpage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.Manifest;
import com.healthfuel.myhealthfuel.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodFragment extends Fragment {
   // private View view;
    private RecyclerView recyclerView;
    private SearchView searchBar;
    private ProgressBar progressBar;
    private ImageButton scanBarcodeButton;
    private static final int REQUEST_CAMERA = 1;
    private String url;
    private ArrayList<Foods> foods = new ArrayList<>();
    private int currentPage;
    private JsonObjectRequest request;
    private FoodAdapter adapter;
    private static final int REQUEST_BARCODE_SCAN = 123;
    ImageButton backButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_food, container, false);

        recyclerView = view.findViewById(R.id.foodsRecyclerView);
        searchBar = view.findViewById(R.id.searchBar);
        progressBar = view.findViewById(R.id.progressBar7);
        scanBarcodeButton = view.findViewById(R.id.scanBarcode);
        backButton = view.findViewById(R.id.backButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FoodAdapter(requireContext(), foods);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                NavOptions navOptions = new NavOptions.Builder().setEnterAnim(R.anim.slide_in_left).setExitAnim(R.anim.slide_out_right).setPopEnterAnim(R.anim.slide_in_left).setPopExitAnim(R.anim.slide_out_right).build();
                navController.navigate(R.id.action_foodFragment_to_homeFragment,null,navOptions);
            }
        });

        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    // Daca permisiunea este deja acordata, deschidem activitatea pentru scanarea codului de bare
                    Intent intent = new Intent(requireContext(), ScanBarcodeActivity.class);
                    startActivityForResult(intent, REQUEST_BARCODE_SCAN);
                }
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPage = 1;
                foods.clear();
                url = "https://ro.openfoodfacts.org/cgi/search.pl?search_terms=" + query + "&fields=product_name,image_url,_id,nutriments&json=1";
                if (request != null) {
                    request.cancel();
                }
                fetchDataFromAPI(url);
                searchBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    private void fetchDataFromAPI(String url) {
        String fullUrl = url + "&page=" + currentPage;
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        request = new JsonObjectRequest(Request.Method.GET, fullUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray productsArray = response.getJSONArray("products");
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject product = productsArray.getJSONObject(i);
                        if (product.has("product_name") && product.has("image_url") && product.has("_id")) {
                            String productName = product.getString("product_name");
                            String imageURL = product.getString("image_url");
                            String barcode = product.getString("_id");

                            // preluam datele din obiectul JSNON si le stocam in ArrayList-ul foods
                            if (product.has("nutriments")) {
                                JSONObject nutriments = product.getJSONObject("nutriments");
                                Double carbohydrates;
                                Integer kcal;
                                Double fat;
                                Double proteins;
                                Double fiber;
                                Double sugar;
                                Double salt;
                                if (nutriments.has("carbohydrates")) {
                                    carbohydrates = nutriments.getDouble("carbohydrates");
                                }
                                else{
                                     carbohydrates = null;
                                }

                                if (nutriments.has("energy")) {
                                    double kj = nutriments.getDouble("energy");
                                    kcal = (int) Math.round(kj/4.2);
                                }
                                else{
                                    kcal = null;
                                }

                                if (nutriments.has("fat")) {
                                    fat = nutriments.getDouble("fat");
                                }
                                else{
                                    fat = null;
                                }
                                if (nutriments.has("proteins")) {
                                    proteins = nutriments.getDouble("proteins");
                                }
                                else{
                                    proteins = null;
                                }
                                if (nutriments.has("fiber")) {
                                    fiber = nutriments.getDouble("fiber");
                                }
                                else{
                                    fiber = null;
                                }
                                if (nutriments.has("sugars")) {
                                    sugar = nutriments.getDouble("sugars");
                                }
                                else{
                                    sugar = null;
                                }
                                if (nutriments.has("salt")) {
                                    salt = nutriments.getDouble("salt");
                                }
                                else{
                                    salt = null;
                                }
                                foods.add(new Foods(productName, imageURL, barcode, carbohydrates, kcal, fat, proteins, fiber, sugar, salt,100));

                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int itemsPerPage = response.getInt("page_size");
                    int itemsTotalNr = response.getInt("count");
                    int MAX_PAGES = 20; // pentru a diminua sansele de outOfMemory
                    double pageNr = (double) itemsTotalNr / itemsPerPage;
                    if (currentPage < Math.min(pageNr, MAX_PAGES)){
                        currentPage++;
                        fetchDataFromAPI(url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (foods.isEmpty()) {
                        Toast.makeText(getContext(), "No item found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (getContext() != null) {
                    if (error instanceof TimeoutError) {
                        Toast.makeText(getContext(), "Timeout Error: Please try again later", Toast.LENGTH_SHORT).show();
                    } else if (error.networkResponse != null && error.networkResponse.statusCode == 502) {
                        Toast.makeText(getContext(), "Server Error: Please try again later", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof NoConnectionError || error instanceof NetworkError) {
                        Toast.makeText(getContext(), "Connection Error: Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        Volley.newRequestQueue(requireContext()).add(request);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BARCODE_SCAN && resultCode == Activity.RESULT_OK) {
            // Obtinem codul de bare scanat
            String barcode = data.getStringExtra("barcode");
            // Apelam metoda pentru a prelua informatiile despre produs folosind codul de bare
            fetchDataFromAPI_barcode(barcode);
        }
    }

    private void fetchDataFromAPI_barcode(String barcode) {
        foods.clear();
        String url = "https://world.openfoodfacts.org/api/v2/product/"+barcode+".json";
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject product = response.getJSONObject("product");
                    if (product.has("product_name") && product.has("image_url") && product.has("_id")) {
                        String productName = product.getString("product_name");
                        String imageURL = product.getString("image_url");
                        String barcode = product.getString("_id");
                        if (product.has("nutriments")) {
                            JSONObject nutriments = product.getJSONObject("nutriments");
                            Double carbohydrates;
                            Integer kcal;
                            Double fat;
                            Double proteins;
                            Double fiber;
                            Double sugar;
                            Double salt;
                            if (nutriments.has("carbohydrates")) {
                                carbohydrates = nutriments.getDouble("carbohydrates");
                            } else {
                                carbohydrates = null;
                            }

                            if (nutriments.has("energy")) {
                                double kj = nutriments.getDouble("energy");
                                kcal = (int) Math.round(kj / 4.2);
                            } else {
                                kcal = null;
                            }

                            if (nutriments.has("fat")) {
                                fat = nutriments.getDouble("fat");
                            } else {
                                fat = null;
                            }
                            if (nutriments.has("proteins")) {
                                proteins = nutriments.getDouble("proteins");
                            } else {
                                proteins = null;
                            }
                            if (nutriments.has("fiber")) {
                                fiber = nutriments.getDouble("fiber");
                            } else {
                                fiber = null;
                            }
                            if (nutriments.has("sugars")) {
                                sugar = nutriments.getDouble("sugars");
                            } else {
                                sugar = null;
                            }
                            if (nutriments.has("salt")) {
                                salt = nutriments.getDouble("salt");
                            } else {
                                salt = null;
                            }
                            foods.add(new Foods(productName, imageURL, barcode, carbohydrates, kcal, fat, proteins, fiber, sugar, salt, 100));

                        }

                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (foods.isEmpty()) {
                        Toast.makeText(getContext(), "No item found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Toast.makeText(getContext(), "Timeout Error: Please try again later", Toast.LENGTH_SHORT).show();
                } else if (error.networkResponse != null && error.networkResponse.statusCode == 502) {
                    Toast.makeText(getContext(), "Server Error: Please try again later", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError || error instanceof NetworkError) {
                    Toast.makeText(getContext(), "Connection Error: Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                } else {
                    error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        Volley.newRequestQueue(requireContext()).add(request);
    }

}
