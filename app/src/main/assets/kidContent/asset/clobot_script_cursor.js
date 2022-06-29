apx.addEventListener('pageBubble', function(Event, ctx) {
	
	with(ctx) {
		if(!Event.target) {
            if(Event.type === 'Custom Event') {
                onCustomEvent(Event.param.trim());
            }
        }
    }

    /**
     * Custom Event 발생
     * 
     * 메시지는 아래와 같은 형태로 발생
     * 
     * ex)ROBOT:Normal/Front/Default
    */
    function onCustomEvent(/*String*/param) {

        //Event Param의 Data Prefix
        var prefixParam = 'ROBOT:';

        if(param && param.indexOf(prefixParam) === 0) {

            var arrData = param.replace(prefixParam, '').split('/');

            if(arrData.length < 3) {
                return;
            }

            var msgForRobot = {
                type: 'robot'
                , value: {
                    p_eye: arrData[0].trim() // Normal, Angry, Boring, Curious, Fear, Gloomy, Happy, Proud, Sad, Shock, Sleep, Sorry, Thanks, Worried
                    , p_head: arrData[1].trim() // Front, Nod, Bow, Shake, Around_Short, Around_Long, Left, Right, Up, Down
                    , p_leg: arrData[2].trim() // Default, Moving, Charging
                }
            }

            window.parent.postMessage(msgForRobot, '*');

            // console.error(JSON.stringify(msgForRobot, null, 4));
        }
        else if(param === 'END_CONTENT') {  //콘텐츠 완료 신호
            window.parent.postMessage({type: 'content', value: 'end'}, '*');
        }
    }
});


//Body의 커서를 숨김
window.addEventListener('DOMContentLoaded', function() {
    document.body.style.cursor = 'none'; 
});

apx.addEventListener('pageBuild', function(dtxPage, APX)
{
    //해당 페이지의 위젯 데이터
	var objs = dtxPage.data.project.pages[dtxPage.id].objects;
	
	for(var i in objs) {
	    //모든 위젯의 커서를 숨김
	    if(objs[i].create && objs[i].create.data && objs[i].create.data.styles) {
            objs[i].create.data.styles.csr = 'none';
	    }
	}
});