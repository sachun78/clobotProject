package com.lge.support.second.application.main.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class ActionType {
    Test, Test2
}
//데이터 변경 가능하게 ?? Mutable로 선언
// 뷰모델 안에 데이터의 변경 사항을 알려주는 liveData
class TkTestViewModel : ViewModel() {

    companion object{
        const val TAG: String = "logForTest"
    }
    //Mutable Live Data(수정 가능한 거) 관리
    //내부에서 설정하는 자료형 => mutable
    private val _currentValue = MutableLiveData<Int>() //일단 int라고 해 뒀는데 robot battery나... movement...

    //live Data (수정 불가 **읽기 전용**) 따로 관리 해줘야 함
    /*변경되지 않는 (mutable 안 붙은 거) 가져 올 때는 _없이 설정
    **공개적으로 가져오는 변수는 private이 아닌 public (외부에서 접근 가능)
    **하지만 값을 직접 liveData에 접근하지 않고 뷰모델을 통해 가져올 수 있도록 설정 */
    val currentValue: LiveData<Int>
        get() = _currentValue

    //초기값 설정
    init {
        Log.i(TAG, "TkTestViewModel 생성자 호출")
        _currentValue.value = 0
        Log.i(TAG, "TkTestViewModel 초기값 설정 (0)")
    }

    fun updateValue(actionType: ActionType, input: Int){
        when(actionType){
            ActionType.Test -> _currentValue.value = _currentValue.value?.plus(input)
//            ActionType.Test2 -> _currentValue.value = _currentValue.value?.minus(input)
        }
    }

}