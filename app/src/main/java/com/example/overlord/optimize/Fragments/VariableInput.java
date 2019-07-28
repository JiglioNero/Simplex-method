package com.example.overlord.optimize.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.overlord.optimize.Arithmetics.Number;
import com.example.overlord.optimize.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VariableInput.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VariableInput#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VariableInput extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "row";
    public static final String ARG_PARAM2 = "index";
    public static final String ARG_PARAM3 = "coefficient";
    public static final String ARG_PARAM4 = "isOnlyNumber";

    // TODO: Rename and change types of parameters
    private int row = -1;
    private int index = 0;
    private Number coefficient = new Number(0);
    private boolean isOnlyNumber = false;

    private TextView indexV;
    private TextView textViewX;
    private TextInputEditText coefficientV;

    private OnFragmentInteractionListener mListener;

    public VariableInput() {

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index       Parameter 1.
     * @param coefficient Parameter 2.
     * @return A new instance of fragment VariableInput.
     */
    // TODO: Rename and change types and number of parameters
    public static VariableInput newInstance(int row, int index, Number coefficient, boolean isOnlyNumber) {
        VariableInput fragment = new VariableInput();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, row);
        args.putInt(ARG_PARAM2, index);
        args.putSerializable(ARG_PARAM3, coefficient);
        args.putSerializable(ARG_PARAM4, isOnlyNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            row = getArguments().getInt(ARG_PARAM1);
            index = getArguments().getInt(ARG_PARAM2);
            coefficient = (Number) getArguments().getSerializable(ARG_PARAM3);
            isOnlyNumber = getArguments().getBoolean(ARG_PARAM4);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_variable_input, container, false);

        coefficientV = view.findViewById(R.id.xCoefficient);
        coefficientV.setText(coefficient.toString());
        coefficientV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coefficientV.selectAll();
            }
        });
        coefficientV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Number.getPattern().matcher(s).matches()) {
                    coefficient = Number.readNumber(s.toString());
                    view.setBackgroundResource(R.drawable.ic_remove_24dp);
                } else {
                    coefficient = null;
                    view.setBackgroundResource(R.drawable.selection);
                }
                Bundle data = new Bundle();
                data.putInt(ARG_PARAM1, row);
                data.putInt(ARG_PARAM2, index);
                data.putSerializable(ARG_PARAM3, coefficient);
                mListener.onFragmentIteration(data);
            }
        });


        indexV = view.findViewById(R.id.xIndex);
        indexV.setText(String.valueOf(index));
        textViewX = view.findViewById(R.id.textViewX);

        if(isOnlyNumber){
            indexV.setText("");
            textViewX.setText("");
            indexV.setVisibility(View.INVISIBLE);
            textViewX.setVisibility(View.INVISIBLE);
        }

        Bundle data = new Bundle();
        data.putInt(ARG_PARAM1, row);
        data.putInt(ARG_PARAM2, index);
        data.putSerializable(ARG_PARAM3, coefficient);
        mListener.onFragmentIteration(data);

        return view;
    }


    public int getRow() {
        return row;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        indexV.setText(String.valueOf(index));
    }

    public Number getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Number coefficient) {
        this.coefficient = coefficient;
        if(mListener!=null) {
            coefficientV.setText(coefficient.toString());
            Bundle data = new Bundle();
            data.putInt(ARG_PARAM1, row);
            data.putInt(ARG_PARAM2, index);
            data.putSerializable(ARG_PARAM3, coefficient);
            mListener.onFragmentIteration(data);
        }
        else {
            getArguments().putSerializable(ARG_PARAM3, coefficient);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentIteration(Bundle data);
    }
}
