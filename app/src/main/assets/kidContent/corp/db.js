window.apn=window.apn||{},apn.dbUI={apx:{wgtEditorStyle:{icon:"DB/blg/imgs/wgts/common/icon.png",iconThumb:"DB/blg/imgs/wgts/common/icon_thumb.png",color:"rgb(171,100,0)"}},brand:{title:"블루가",logoFull:"DB/blg/imgs/corp/logoFull.png",logoTitle:"DB/blg/imgs/corp/logoTitle.png",corpURL:"http://www.bluega.com"},system:{exeItrURL:!0,exeEventHover:!0,exeEventStart:!0,exeTxtUnderlined:"border",exeScripting:2,exeScriptingDomain:"http://pvp1.wren.kr",exeScriptingDefault:1,exeCompress:!0,exePatchOverlap:1,exeDOM2:!0,exeEventCache:!0,exePostPageCreateOnPreload:!0,pubVersion:"2",pubFontCompress:!0,pubCustomHTML:!0,scrZoomRatio:4,scrPageTemplateAdd:!0,scrUseLetterSpacing:!0,scrUseLineSpacing:!0,scrUseCategoryFile:!0,scrUseCategoryTemplate:!0,scrUseCategoryPageTemplate:!0,scrExternalFileStream:[{title:{en:"From PDF File",ko:"PDF File로부터"},titleBar:{en:"Import from PDF File",ko:"PDF File로부터 들여오기"},type:"import",url:"http://blg.aspenux.com/tool/pdf.aspen/index.html",w:"1320px",h:"96%",help:{URL:{ko:"http://aspenux.com/kb/EDT/B9/index.html?ABRDOCBAR=T"}}}],apdEditorPresence:{fileType:"ZIP",level:"EDU",merge:!1}},device:"EDU",access:{admin:{admUse:!0,scrUseTemplateFile:!0,scrUsePgTemplateFile:!0,fileStorage:"S",fileTemplateFile:!0,scrSaveNavHistory:!1,rscImageDeposit:"S",rscAudioDeposit:"S",rscVideoDeposit:"S",rscImageLimit:3e6,rscAudioLimit:2e7,rscVideoLimit:5e7,rscSvgLimit:8e5,rscPdf2Limit:2e7,flwUseRefresh:!0,exeUseConsole:!0,exeEventDrag:!0},first:{scrUseTemplateFile:!0,rscImageSharedUpdate:!0,rscAudioSharedUpdate:!0,scrUsePgTemplateFile:!0,fileStorage:"S",fileTemplateFile:!0,scrSaveNavHistory:!1,rscImageDeposit:"S",rscAudioDeposit:"S",rscVideoDeposit:"S",rscImageLimit:3e6,rscAudioLimit:2e7,rscVideoLimit:5e7,rscSvgLimit:8e5,rscPdf2Limit:2e7,flwUseRefresh:!0,exeUseConsole:!0,exeEventDrag:!0},second:{fileStorage:"S",fileTemplateFile:!0,scrSaveNavHistory:!1,rscImageDeposit:"S",rscAudioDeposit:"S",rscVideoDeposit:"S",rscImageLimit:3e6,rscAudioLimit:2e7,rscVideoLimit:5e7,rscSvgLimit:8e5,rscPdf2Limit:2e7,flwUseRefresh:!0,exeUseConsole:!0,exeEventDrag:!0}}},apn.widgetLibraryFilter=function(e){if(apn.dbUI&&(apn.dbHelpMenu({KB:["APX","ABR","PDF","B1G","SGG","Y8M"]}),apn.dbUI.access.admin.tools=apn.dbUI.access.first.tools=apn.dbUI.access.second.tools=[{title:{en:"Aspen PDF for Aspen Reader ...",ko:"Aspen PDF for Aspen Reader ..."},URL:"tool/pdf.reader/index.html",target:"apn.pdf"},{title:{en:"Aspen PDF ...",ko:"Aspen PDF ..."},URL:"tool/pdf.apn/index.html",target:"apn.pdf.apn"},{title:{en:"Aspen LipSync ...",ko:"Aspen LipSync ..."},URL:"tool/lip.sync/index.html",target:"apn.lip.sync"},{},{title:{en:"Update Tracker Manager ..."},URL:"http://blg.aspenux.com/tracker_admin.html",width:"80%",height:"90%"}],apn.widgetDevice.EDU.font&&(apn.dbUI.font=apn.widgetDevice.EDU.font)),window.asui&&asui.CFG&&asui.CFG.getLogin()&&"3"==asui.CFG.getLogin().memberGroup)for(var t,o=0;o<apn.widgetLibrary.EDU.groups.length;o++)for(t=0;t<apn.widgetLibrary.EDU.groups[o].widgets.length;t++)apn.widgetLibrary.EDU.groups[o].widgets[t].UI&&apn.widgetLibrary.EDU.groups[o].widgets[t].UI.admin&&(apn.widgetLibrary.EDU.groups[o].widgets[t].UI.hide=!0);if(apn.dbUI&&apn.dbUI.apx&&apn.dbUI.apx.wgtEditorStyle&&apn.dbUI.apx.wgtEditorStyle.icon)for(var t,o=0;o<apn.widgetLibrary.EDU.groups.length;o++)for(t=0;t<apn.widgetLibrary.EDU.groups[o].widgets.length;t++)apn.widgetLibrary.EDU.groups[o].widgets[t].UI&&apn.widgetLibrary.EDU.groups[o].widgets[t].UI.icon||(apn.widgetLibrary.EDU.groups[o].widgets[t].UI=apn.widgetLibrary.EDU.groups[o].widgets[t].UI||{},apn.widgetLibrary.EDU.groups[o].widgets[t].UI.icon=apn.dbUI.apx.wgtEditorStyle.icon);for(var o=0;o<apn.widgetLibrary.EDU.groups.length;o++)for(t=0;t<apn.widgetLibrary.EDU.groups[o].widgets.length;t++)apn.widgetLibrary.EDU.groups[o].widgets[t].shape||(apn.widgetLibrary.EDU.groups[o].widgets[t].shape={position:{x:"center",y:"middle"},size:{w:"100%",h:"100%"}});apn.dbHelpApply(),apn.widgetMap()},window.apn=window.apn||{},apn.theme={EDU:{UI:{title:"AspenEdu"},styles:{normal:{fillStyle:"#FFFFFF",strokeStyle:"#7E7E7E",lineWidth:1,lineDash:null,lineEnd:"square",borderRadiusTopLeft:0,borderRadiusTopRight:0,borderRadiusBottomLeft:0,borderRadiusBottomRight:0,font:"Arial",fontSize:"24px",fontStyle:"#000000",fontItalic:!1,fontBold:!1,textMultiLine:!1,fontUnderlined:!1,text:"",textAlign:"center",textValign:"middle",textPadding:8,textWordWrap:!0,textFit:0,fontStrokeStyle:"#FFA500",fontStrokeWidth:0,alpha:1,angle:0,keepRatio:!1,title:"",visibility:!0,dragX:!1,dragY:!1,dragInParent:!1,dragContainParent:!1,ltrSp:0,lnSp:0,csr:"apn.auto",lD2:null,lE2:null}},properties:{trEffectForward:"none",trEffectBackward:"none",trTiming:"ease-in-out",trDuration:500}}},window.apn=window.apn||{},apn.widgetDevice={EDU:{UI:{title:"AspenEdu"},skinID:"Apple",themeID:"EDU",wgtLibID:"EDU",device:{screenSize:[{w:300,h:250,icon:{w:60,h:60},grid:{w:10,h:10}},{w:300,h:600,icon:{w:60,h:60},grid:{w:10,h:10}},{w:320,h:100,icon:{w:60,h:60},grid:{w:10,h:10}},{w:320,h:240,icon:{w:60,h:60},grid:{w:10,h:10}},{w:336,h:280,icon:{w:60,h:60},grid:{w:10,h:10}},{w:400,h:300,icon:{w:60,h:60},grid:{w:10,h:10}},{w:480,h:320,icon:{w:60,h:60},grid:{w:10,h:10}},{w:480,h:640,icon:{w:60,h:60},grid:{w:10,h:10}},{w:480,h:800,icon:{w:60,h:60},grid:{w:10,h:10}},{w:520,h:800,icon:{w:60,h:60},grid:{w:10,h:10}},{w:640,h:360,icon:{w:120,h:120},grid:{w:10,h:10}},{w:640,h:480,icon:{w:120,h:120},grid:{w:10,h:10}},{w:640,h:960,icon:{w:120,h:120},grid:{w:10,h:10}},{w:640,h:1136,icon:{w:120,h:120},grid:{w:10,h:10}},{w:720,h:480,icon:{w:120,h:120},grid:{w:10,h:10}},{w:720,h:1280,icon:{w:120,h:120},grid:{w:10,h:10}},{w:728,h:90,icon:{w:120,h:120},grid:{w:10,h:10}},{w:768,h:1024,icon:{w:120,h:120},grid:{w:10,h:10}},{w:800,h:480,icon:{w:120,h:120},grid:{w:10,h:10}},{w:800,h:600,icon:{w:120,h:120},grid:{w:10,h:10}},{w:810,h:800,icon:{w:60,h:60},grid:{w:10,h:10}},{w:1080,h:1920,icon:{w:240,h:240},grid:{w:10,h:10}},{w:1024,h:764,icon:{w:120,h:120},grid:{w:10,h:10}},{w:1280,h:720,preset:!0,icon:{w:120,h:120},grid:{w:10,h:10}},{w:1536,h:2048,icon:{w:240,h:240},grid:{w:10,h:10}},{w:1920,h:1080,icon:{w:240,h:240},grid:{w:10,h:10}}]},exeModule:{standard:{title:"Standard",module:"apn.CExe",option:{setupSplash:!0,preloadAsset:{}}},book:{title:"Book",module:"apn.CExeBook",option:{preloadAsset:{},CExeBook:{useNaviBtns:!0}}},apnCExeBookEPUB3:{title:"EPUB3 Book",module:"apn.CExeBookEPUB3",info:"For EPUB3 Book"},apnCExePDF:{title:"PDF Book",module:"apn.CExePDF"},apnCExeBookABR:{title:"ABR Book",module:"apn.CExeBookABR"},abrCExe:{title:"ABR Content",module:"abrCExe",info:"App and Page module for Aspen Reader"},abrCExeBook:{title:"ABR Book Content",module:"abrCExeBook",info:"Book-typed App and Page module for Aspen Reader"},krsCBook:{title:"KERIS Book",module:"krsCBook",info:"KERIS 디지털 교과서"},krsCExe:{title:"KERIS Popup",module:"krsCExe",info:"KERIS 별도 팝업 컨텐츠"}},font:[{title:"Arial(s)",face:"Arial",type:"system",preview:"DB/blg/imgs/fonts/5.png"},{title:"Arial Black(s)",height:1.1,face:"Arial Black",type:"system",preview:"DB/blg/imgs/fonts/6.png"},{title:"Arial Narrow(s)",face:"Arial Narrow",type:"system",preview:"DB/blg/imgs/fonts/7.png"},{title:"Average Sans",face:"Average Sans",noBold:!0,type:"dir",path:"DB/abr/fonts/AverageSans/font.css",preview:"DB/blg/imgs/fonts/average_sans.png",files:{"AverageSans-Regular.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Bree Serif",face:"Bree Serif",type:"dir",path:"DB/blg/fonts/BreeSerif/font.css",preview:"DB/blg/imgs/fonts/bree_serif.png",files:{"BreeSerif-Regular.woff":"application/font-woff","BreeSerif-Bold.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Bubblegum Sans",face:"Bubblegum Sans",noBold:!0,type:"dir",path:"DB/abr/fonts/BubblegumSans/font.css",preview:"DB/blg/imgs/fonts/bubblegum_sans.png",files:{"BubblegumSans-Regular.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Courier New(s)",face:"Courier New",type:"system",preview:"DB/blg/imgs/fonts/8.png"},{title:"Fredoka One",height:1.3,face:"Fredoka One",noBold:!0,type:"dir",path:"DB/abr/fonts/FredokaOne/font.css",preview:"DB/blg/imgs/fonts/fredoka_one.png",files:{"FredokaOne-Regular.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Muli",face:"Muli",type:"dir",path:"DB/blg/fonts/Muli/font.css",preview:"DB/blg/imgs/fonts/muli.png",files:{"Muli-Regular.woff":"application/font-woff","Muli-Bold.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Noto Sans",height:1.3,face:"Noto Sans",type:"dir",path:"DB/abr/fonts/NotoSans/font.css",preview:"DB/blg/imgs/fonts/noto_sans.png",files:{"NotoSans-Regular.woff":"application/font-woff","NotoSans-Bold.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Open Sans",face:"Open Sans",type:"dir",path:"DB/abr/fonts/OpenSans/font.css",preview:"DB/blg/imgs/fonts/10.png",files:{"OpenSans-Regular.woff":"application/font-woff","OpenSans-Bold.woff":"application/font-woff","LICENSE.txt":"text/plain"}},{title:"Palatino(s)",face:"Palatino Linotype",type:"system",preview:"DB/blg/imgs/fonts/11.png"},{title:"Patrick Hand",height:1.3,face:"Patrick Hand",noBold:!0,type:"dir",path:"DB/abr/fonts/PatrickHand/font.css",preview:"DB/blg/imgs/fonts/patrick_hand.png",files:{"PatrickHand-Regular.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Roboto",face:"Roboto",type:"dir",path:"DB/abr/fonts/Roboto/font.css",preview:"DB/blg/imgs/fonts/roboto.png",files:{"Roboto-Regular.woff":"application/font-woff","Roboto-Bold.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Roboto Slab",face:"Roboto Slab",type:"dir",path:"DB/abr/fonts/RobotoSlab/font.css",preview:"DB/blg/imgs/fonts/roboto_slab.png",files:{"RobotoSlab-Regular.woff":"application/font-woff","RobotoSlab-Bold.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"Trebuchet MS(s)",height:1.3,face:"Trebuchet MS",type:"system",preview:"DB/blg/imgs/fonts/13.png"},{title:"Wellfleet",height:1.3,face:"Wellfleet",noBold:!0,type:"dir",path:"DB/abr/fonts/Wellfleet/font.css",preview:"DB/blg/imgs/fonts/wellfleet.png",files:{"Wellfleet-Regular.woff":"application/font-woff","OFL.txt":"text/plain"}},{title:"나눔 고딕",height:1.3,ln:"ko",face:"Nanum Gothic",type:"dir",path:"DB/abr/fonts/NanumGothic/font.css",preview:"DB/blg/imgs/fonts/1.png",files:{"NanumGothic-Regular.woff":"application/font-woff","NanumGothic-Bold.woff":"application/font-woff"}},{title:"나눔 명조",height:1.3,ln:"ko",face:"Nanum Myeongjo",type:"dir",path:"DB/abr/fonts/NanumMyeongjo/font.css",preview:"DB/blg/imgs/fonts/2.png",files:{"NanumMyeongjo-Regular.woff":"application/font-woff","NanumMyeongjo-ExtraBold.woff":"application/font-woff"}},{title:"나눔 바른고딕",height:1.3,ln:"ko",face:"Nanum Barun Gothic",type:"dir",path:"DB/abr/fonts/NanumBarunGothic/font.css",preview:"DB/blg/imgs/fonts/nanum_barun_gothic.png",files:{"NanumBarunGothic.woff":"application/font-woff","NanumBarunGothicBold.woff":"application/font-woff"}},{title:"나눔 Pen Script",height:1.1,noBold:!0,ln:"ko",face:"Nanum Pen Script",type:"dir",path:"DB/abr/fonts/NanumPen/font.css",preview:"DB/blg/imgs/fonts/3.png",files:{"NanumPen-Regular.woff":"application/font-woff"}},{title:"나눔 Brush",height:1.1,noBold:!0,ln:"ko",face:"Nanum Brush Script",type:"dir",path:"DB/abr/fonts/NanumBrush/font.css",preview:"DB/blg/imgs/fonts/4.png",files:{"NanumBrushScript-Regular.woff":"application/font-woff"}},{title:"주아체(i)",ln:"ko",face:"BM Jua",type:"dir",noBold:!0,path:"DB/blg/fonts/Jua/font.css",preview:"DB/blg/imgs/fonts/jua.png",files:{"bm_jua.woff":"application/font-woff"}}]}};