package com.alex.tictacpark.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alex.tictacpark.R;
import com.alex.tictacpark.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EstadoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EstadoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstadoFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param SectionNumber Indica el número de la sección pulsada.
     * @return A new instance of fragment EstadoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EstadoFragment newInstance(int SectionNumber) {
        EstadoFragment fragment = new EstadoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, SectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public EstadoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        //Asignamos un layout
        rootView=inflater.inflate(R.layout.fragment_estado, container, false); //layout

        // Ponemos el nombre "Estado" en la barra
        ((MainActivity) getActivity()).setActionBarTitle("Estado");

        ViewGroup.LayoutParams params = rootView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        rootView.setLayoutParams(params);

        return rootView;
    }

     /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
