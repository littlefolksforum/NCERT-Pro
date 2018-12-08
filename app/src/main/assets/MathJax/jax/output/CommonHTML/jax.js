/*
 *  /MathJax/jax/output/CommonHTML/jax.js
 *
 *  Copyright (c) 2009-2015 The MathJax Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function(i,b,e,g){var h;var j,a,d;var f="'Times New Roman',Times,STIXGeneral,serif";var m={".MJXc-script":{"font-size":".8em"},".MJXc-right":{"-webkit-transform-origin":"right","-moz-transform-origin":"right","-ms-transform-origin":"right","-o-transform-origin":"right","transform-origin":"right"},".MJXc-bold":{"font-weight":"bold"},".MJXc-italic":{"font-style":"italic"},".MJXc-scr":{"font-family":"MathJax_Script,"+f},".MJXc-frak":{"font-family":"MathJax_Fraktur,"+f},".MJXc-sf":{"font-family":"MathJax_SansSerif,"+f},".MJXc-cal":{"font-family":"MathJax_Caligraphic,"+f},".MJXc-mono":{"font-family":"MathJax_Typewriter,"+f},".MJXc-largeop":{"font-size":"150%"},".MJXc-largeop.MJXc-int":{"vertical-align":"-.2em"},".MJXc-math":{display:"inline-block","line-height":"1.2","text-indent":"0","font-family":f,"white-space":"nowrap","border-collapse":"collapse"},".MJXc-display":{display:"block","text-align":"center",margin:"1em 0"},".MJXc-math span":{display:"inline-block"},".MJXc-box":{display:"block!important","text-align":"center"},".MJXc-box:after":{content:'" "'},".MJXc-rule":{display:"block!important","margin-top":".1em"},".MJXc-char":{display:"block!important"},".MJXc-mo":{margin:"0 .15em"},".MJXc-mfrac":{margin:"0 .125em","vertical-align":".25em"},".MJXc-denom":{display:"inline-table!important",width:"100%"},".MJXc-denom > *":{display:"table-row!important"},".MJXc-surd":{"vertical-align":"top"},".MJXc-surd > *":{display:"block!important"},".MJXc-script-box > * ":{display:"table!important",height:"50%"},".MJXc-script-box > * > *":{display:"table-cell!important","vertical-align":"top"},".MJXc-script-box > *:last-child > *":{"vertical-align":"bottom"},".MJXc-script-box > * > * > *":{display:"block!important"},".MJXc-mphantom":{visibility:"hidden"},".MJXc-munderover":{display:"inline-table!important"},".MJXc-over":{display:"inline-block!important","text-align":"center"},".MJXc-over > *":{display:"block!important"},".MJXc-munderover > *":{display:"table-row!important"},".MJXc-mtable":{"vertical-align":".25em",margin:"0 .125em"},".MJXc-mtable > *":{display:"inline-table!important","vertical-align":"middle"},".MJXc-mtr":{display:"table-row!important"},".MJXc-mtd":{display:"table-cell!important","text-align":"center",padding:".5em 0 0 .5em"},".MJXc-mtr > .MJXc-mtd:first-child":{"padding-left":0},".MJXc-mtr:first-child > .MJXc-mtd":{"padding-top":0},".MJXc-mlabeledtr":{display:"table-row!important"},".MJXc-mlabeledtr > .MJXc-mtd:first-child":{"padding-left":0},".MJXc-mlabeledtr:first-child > .MJXc-mtd":{"padding-top":0},".MJXc-merror":{"background-color":"#FFFF88",color:"#CC0000",border:"1px solid #CC0000",padding:"1px 3px","font-style":"normal","font-size":"90%"}};(function(){for(var n=0;n<10;n++){var o="scaleX(."+n+")";m[".MJXc-scale"+n]={"-webkit-transform":o,"-moz-transform":o,"-ms-transform":o,"-o-transform":o,transform:o}}})();var k=1000000;var c="V",l="H";g.Augment({settings:b.config.menuSettings,config:{styles:m},hideProcessedMath:false,maxStretchyParts:1000,Config:function(){if(!this.require){this.require=[]}this.SUPER(arguments).Config.call(this);var n=this.settings;if(n.scale){this.config.scale=n.scale}this.require.push(MathJax.OutputJax.extensionDir+"/MathEvents.js")},Startup:function(){j=MathJax.Extension.MathEvents.Event;a=MathJax.Extension.MathEvents.Touch;d=MathJax.Extension.MathEvents.Hover;this.ContextMenu=j.ContextMenu;this.Mousedown=j.AltContextMenu;this.Mouseover=d.Mouseover;this.Mouseout=d.Mouseout;this.Mousemove=d.Mousemove;var n=e.addElement(document.body,"div",{style:{width:"5in"}});this.pxPerInch=n.offsetWidth/5;n.parentNode.removeChild(n);return i.Styles(this.config.styles,["InitializeCHTML",this])},InitializeCHTML:function(){},preTranslate:function(p){var s=p.jax[this.id],t,q=s.length,u,r,v,o,n;for(t=0;t<q;t++){u=s[t];if(!u.parentNode){continue}r=u.previousSibling;if(r&&String(r.className).match(/^MathJax_CHTML(_Display)?( MathJax_Processing)?$/)){r.parentNode.removeChild(r)}n=u.MathJax.elementJax;if(!n){continue}n.CHTML={display:(n.root.Get("display")==="block")};v=o=e.Element("span",{className:"MathJax_CHTML",id:n.inputID+"-Frame",isMathJax:true,jaxID:this.id,oncontextmenu:j.Menu,onmousedown:j.Mousedown,onmouseover:j.Mouseover,onmouseout:j.Mouseout,onmousemove:j.Mousemove,onclick:j.Click,ondblclick:j.DblClick});if(b.Browser.noContextMenu){v.ontouchstart=a.start;v.ontouchend=a.end}if(n.CHTML.display){o=e.Element("div",{className:"MathJax_CHTML_Display"});o.appendChild(v)}o.className+=" MathJax_Processing";u.parentNode.insertBefore(o,u)}},Translate:function(o,s){if(!o.parentNode){return}var n=o.MathJax.elementJax,r=n.root,p=document.getElementById(n.inputID+"-Frame"),t=(n.CHTML.display?p.parentNode:p);this.initCHTML(r,p);try{r.toCommonHTML(p)}catch(q){if(q.restart){while(p.firstChild){p.removeChild(p.firstChild)}}throw q}t.className=t.className.split(/ /)[0];if(this.hideProcessedMath){t.className+=" MathJax_Processed";if(o.MathJax.preview){n.CHTML.preview=o.MathJax.preview;delete o.MathJax.preview}}},postTranslate:function(s){var o=s.jax[this.id];if(!this.hideProcessedMath){return}for(var q=0,n=o.length;q<n;q++){var p=o[q];if(p&&p.MathJax.elementJax){p.previousSibling.className=p.previousSibling.className.split(/ /)[0];var r=p.MathJax.elementJax.CHTML;if(r.preview){r.preview.innerHTML="";p.MathJax.preview=r.preview;delete r.preview}}}},getJaxFromMath:function(n){if(n.parentNode.className==="MathJax_CHTML_Display"){n=n.parentNode}do{n=n.nextSibling}while(n&&n.nodeName.toLowerCase()!=="script");return b.getJaxFor(n)},getHoverSpan:function(n,o){return n.root.CHTMLspanElement()},getHoverBBox:function(n,o,p){return BBOX},Zoom:function(o,u,s,n,r){u.className="MathJax";this.idPostfix="-zoom";o.root.toCHTML(u,u);this.idPostfix="";u.style.position="absolute";if(!width){s.style.position="absolute"}var t=u.offsetWidth,q=u.offsetHeight,v=s.offsetHeight,p=s.offsetWidth;if(p===0){p=s.parentNode.offsetWidth}u.style.position=s.style.position="";return{Y:-j.getBBox(u).h,mW:p,mH:v,zW:t,zH:q}},initCHTML:function(o,n){},Remove:function(n){var o=document.getElementById(n.inputID+"-Frame");if(o){if(n.CHTML.display){o=o.parentNode}o.parentNode.removeChild(o)}delete n.CHTML},ID:0,idPostfix:"",GetID:function(){this.ID++;return this.ID},VARIANT:{bold:"MJXc-bold",italic:"MJXc-italic","bold-italic":"MJXc-bold MJXc-italic",script:"MJXc-scr","bold-script":"MJXc-scr MJXc-bold",fraktur:"MJXc-frak","bold-fraktur":"MJXc-frak MJXc-bold",monospace:"MJXc-mono","sans-serif":"MJXc-sf","-tex-caligraphic":"MJXc-cal"},MATHSPACE:{veryverythinmathspace:1/18,verythinmathspace:2/18,thinmathspace:3/18,mediummathspace:4/18,thickmathspace:5/18,verythickmathspace:6/18,veryverythickmathspace:7/18,negativeveryverythinmathspace:-1/18,negativeverythinmathspace:-2/18,negativethinmathspace:-3/18,negativemediummathspace:-4/18,negativethickmathspace:-5/18,negativeverythickmathspace:-6/18,negativeveryverythickmathspace:-7/18,thin:0.08,medium:0.1,thick:0.15,infinity:k},TeX:{x_height:0.430554},pxPerInch:72,em:16,DELIMITERS:{"(":{dir:c},"{":{dir:c,w:0.58},"[":{dir:c},"|":{dir:c,w:0.275},")":{dir:c},"}":{dir:c,w:0.58},"]":{dir:c},"/":{dir:c},"\\":{dir:c},"\u2223":{dir:c,w:0.275},"\u2225":{dir:c,w:0.55},"\u230A":{dir:c,w:0.5},"\u230B":{dir:c,w:0.5},"\u2308":{dir:c,w:0.5},"\u2309":{dir:c,w:0.5},"\u27E8":{dir:c,w:0.5},"\u27E9":{dir:c,w:0.5},"\u2191":{dir:c,w:0.65},"\u2193":{dir:c,w:0.65},"\u21D1":{dir:c,w:0.75},"\u21D3":{dir:c,w:0.75},"\u2195":{dir:c,w:0.65},"\u21D5":{dir:c,w:0.75},"\u27EE":{dir:c,w:0.275},"\u27EF":{dir:c,w:0.275},"\u23B0":{dir:c,w:0.6},"\u23B1":{dir:c,w:0.6}},REMAPACCENT:{"\u20D7":"\u2192","'":"\u02CB","`":"\u02CA",".":"\u02D9","^":"\u02C6","-":"\u02C9","~":"\u02DC","\u00AF":"\u02C9","\u00B0":"\u02DA","\u00B4":"\u02CA","\u0300":"\u02CB","\u0301":"\u02CA","\u0302":"\u02C6","\u0303":"\u02DC","\u0304":"\u02C9","\u0305":"\u02C9","\u0306":"\u02D8","\u0307":"\u02D9","\u0308":"\u00A8","\u030C":"\u02C7"},REMAPACCENTUNDER:{},length2em:function(r,p){if(typeof(r)!=="string"){r=r.toString()}if(r===""){return""}if(r===h.SIZE.NORMAL){return 1}if(r===h.SIZE.BIG){return 2}if(r===h.SIZE.SMALL){return 0.71}if(this.MATHSPACE[r]){return this.MATHSPACE[r]}var o=r.match(/^\s*([-+]?(?:\.\d+|\d+(?:\.\d*)?))?(pt|em|ex|mu|px|pc|in|mm|cm|%)?/);var n=parseFloat(o[1]||"1"),q=o[2];if(p==null){p=1}if(q==="em"){return n}if(q==="ex"){return n*this.TeX.x_height}if(q==="%"){return n/100*p}if(q==="px"){return n/this.em}if(q==="pt"){return n/10}if(q==="pc"){return n*1.2}if(q==="in"){return n*this.pxPerInch/this.em}if(q==="cm"){return n*this.pxPerInch/this.em/2.54}if(q==="mm"){return n*this.pxPerInch/this.em/25.4}if(q==="mu"){return n/18}return n*p},Em:function(n){if(Math.abs(n)<0.001){return"0em"}return(n.toFixed(3).replace(/\.?0+$/,""))+"em"},arrayEntry:function(n,o){return n[Math.max(0,Math.min(o,n.length-1))]}});MathJax.Hub.Register.StartupHook("mml Jax Ready",function(){h=MathJax.ElementJax.mml;h.mbase.Augment({toCommonHTML:function(o,n){return this.CHTMLdefaultSpan(o,n)},CHTMLdefaultSpan:function(q,o){if(!o){o={}}q=this.CHTMLcreateSpan(q);this.CHTMLhandleStyle(q);this.CHTMLhandleColor(q);if(this.isToken){this.CHTMLhandleToken(q)}for(var p=0,n=this.data.length;p<n;p++){this.CHTMLaddChild(q,p,o)}return q},CHTMLaddChild:function(p,o,n){var q=this.data[o];if(q){if(n.childSpans){p=e.addElement(p,"span",{className:n.className})}q.toCommonHTML(p);if(!n.noBBox){this.CHTML.w+=q.CHTML.w+q.CHTML.l+q.CHTML.r;if(q.CHTML.h>this.CHTML.h){this.CHTML.h=q.CHTML.h}if(q.CHTML.d>this.CHTML.d){this.CHTML.d=q.CHTML.d}if(q.CHTML.t>this.CHTML.t){this.CHTML.t=q.CHTML.t}if(q.CHTML.b>this.CHTML.b){this.CHTML.b=q.CHTML.b}}}else{if(n.forceChild){e.addElement(p,"span")}}},CHTMLstretchChild:function(q,p,s){var r=this.data[q];if(r&&r.CHTMLcanStretch("Vertical",p,s)){var t=this.CHTML,o=r.CHTML,n=o.w;r.CHTMLstretchV(p,s);t.w+=o.w-n;if(o.h>t.h){t.h=o.h}if(o.d>t.d){t.d=o.d}}},CHTMLcreateSpan:function(n){if(!this.CHTML){this.CHTML={}}this.CHTML={w:0,h:0,d:0,l:0,r:0,t:0,b:0};if(this.inferred){return n}if(this.type==="mo"&&this.data.join("")==="\u222B"){g.lastIsInt=true}else{if(this.type!=="mspace"||this.width!=="negativethinmathspace"){g.lastIsInt=false}}if(!this.CHTMLspanID){this.CHTMLspanID=g.GetID()}var o=(this.id||"MJXc-Span-"+this.CHTMLspanID);return e.addElement(n,"span",{className:"MJXc-"+this.type,id:o})},CHTMLspanElement:function(){if(!this.CHTMLspanID){return null}return document.getElementById(this.id||"MJXc-Span-"+this.CHTMLspanID)},CHTMLhandleToken:function(o){var n=this.getValues("mathvariant");if(n.mathvariant!==h.VARIANT.NORMAL){o.className+=" "+g.VARIANT[n.mathvariant]}},CHTMLhandleStyle:function(n){if(this.style){n.style.cssText=this.style}},CHTMLhandleColor:function(n){if(this.mathcolor){n.style.color=this.mathcolor}if(this.mathbackground){n.style.backgroundColor=this.mathbackground}},CHTMLhandleScriptlevel:function(n){var o=this.Get("scriptlevel");if(o){n.className+=" MJXc-script"}},CHTMLhandleText:function(y,A){var v,p;var z=0,o=0,q=0;for(var s=0,r=A.length;s<r;s++){p=A.charCodeAt(s);v=A.charAt(s);if(p>=55296&&p<56319){s++;p=(((p-55296)<<10)+(A.charCodeAt(s)-56320))+65536}var t=0.7,u=0.22,x=0.5;if(p<127){if(v.match(/[A-Za-ehik-or-xz0-9]/)){u=0}if(v.match(/[A-HK-Z]/)){x=0.67}else{if(v.match(/[IJ]/)){x=0.36}}if(v.match(/[acegm-su-z]/)){t=0.45}else{if(v.match(/[ij]/)){t=0.75}}if(v.match(/[ijlt]/)){x=0.28}}if(g.DELIMITERS[v]){x=g.DELIMITERS[v].w||0.4}if(t>z){z=t}if(u>o){o=u}q+=x}if(!this.CHML){this.CHTML={}}this.CHTML={h:0.9,d:0.3,w:q,l:0,r:0,t:z,b:o};e.addText(y,A)},CHTMLbboxFor:function(o){if(this.data[o]&&this.data[o].CHTML){return this.data[o].CHTML}return{w:0,h:0,d:0,l:0,r:0,t:0,b:0}},CHTMLcanStretch:function(q,o,p){if(this.isEmbellished()){var n=this.Core();if(n&&n!==this){return n.CHTMLcanStretch(q,o,p)}}return false},CHTMLstretchV:function(n,o){},CHTMLstretchH:function(n){},CoreParent:function(){var n=this;while(n&&n.isEmbellished()&&n.CoreMO()===this&&!n.isa(h.math)){n=n.Parent()}return n},CoreText:function(n){if(!n){return""}if(n.isEmbellished()){return n.CoreMO().data.join("")}while((n.isa(h.mrow)||n.isa(h.TeXAtom)||n.isa(h.mstyle)||n.isa(h.mphantom))&&n.data.length===1&&n.data[0]){n=n.data[0]}if(!n.isToken){return""}else{return n.data.join("")}}});h.chars.Augment({toCommonHTML:function(n){var o=this.toString().replace(/[\u2061-\u2064]/g,"");this.CHTMLhandleText(n,o)}});h.entity.Augment({toCommonHTML:function(n){var o=this.toString().replace(/[\u2061-\u2064]/g,"");this.CHTMLhandleText(n,o)}});h.math.Augment({toCommonHTML:function(n){n=this.CHTMLdefaultSpan(n);if(this.Get("display")==="block"){n.className+=" MJXc-display"}return n}});h.mo.Augment({toCommonHTML:function(o){o=this.CHTMLdefaultSpan(o);this.CHTMLadjustAccent(o);var n=this.getValues("lspace","rspace","scriptlevel","displaystyle","largeop");if(n.scriptlevel===0){this.CHTML.l=g.length2em(n.lspace);this.CHTML.r=g.length2em(n.rspace);o.style.marginLeft=g.Em(this.CHTML.l);o.style.marginRight=g.Em(this.CHTML.r)}else{this.CHTML.l=0.15;this.CHTML.r=0.1}if(n.displaystyle&&n.largeop){var p=e.Element("span",{className:"MJXc-largeop"});p.appendChild(o.firstChild);o.appendChild(p);this.CHTML.h*=1.2;this.CHTML.d*=1.2;if(this.data.join("")==="\u222B"){p.className+=" MJXc-int"}}return o},CHTMLadjustAccent:function(p){var o=this.CoreParent();if(o&&o.isa(h.munderover)&&this.CoreText(o.data[o.base]).length===1){var q=o.data[o.over],n=o.data[o.under];var s=this.data.join(""),r;if(q&&this===q.CoreMO()&&o.Get("accent")){r=g.REMAPACCENT[s]}else{if(n&&this===n.CoreMO()&&o.Get("accentunder")){r=g.REMAPACCENTUNDER[s]}}if(r){s=p.innerHTML=r}if(s.match(/[\u02C6-\u02DC\u00A8]/)){this.CHTML.acc=-0.52}else{if(s==="\u2192"){this.CHTML.acc=-0.15;this.CHTML.vec=true}}}},CHTMLcanStretch:function(q,o,p){if(!this.Get("stretchy")){return false}var r=this.data.join("");if(r.length>1){return false}r=g.DELIMITERS[r];var n=(r&&r.dir===q.substr(0,1));if(n){n=(this.CHTML.h!==o||this.CHTML.d!==p||(this.Get("minsize",true)||this.Get("maxsize",true)))}return n},CHTMLstretchV:function(p,u){var o=this.CHTMLspanElement(),t=this.CHTML;var n=this.getValues("symmetric","maxsize","minsize");if(n.symmetric){l=2*Math.max(p-0.25,u+0.25)}else{l=p+u}n.maxsize=g.length2em(n.maxsize,t.h+t.d);n.minsize=g.length2em(n.minsize,t.h+t.d);l=Math.max(n.minsize,Math.min(n.maxsize,l));var s=l/(t.h+t.d-0.3);var q=e.Element("span",{style:{"font-size":g.Em(s)}});if(s>1.25){var r=Math.ceil(1.25/s*10);q.className="MJXc-right MJXc-scale"+r;q.style.marginLeft=g.Em(t.w*(r/10-1)+0.07);t.w*=s*r/10}q.appendChild(o.firstChild);o.appendChild(q);if(n.symmetric){o.style.verticalAlign=g.Em(0.25*(1-s))}}});h.mspace.Augment({toCommonHTML:function(q){q=this.CHTMLdefaultSpan(q);var o=this.getValues("height","depth","width");var n=g.length2em(o.width),p=g.length2em(o.height),s=g.length2em(o.depth);var r=this.CHTML;r.w=n;r.h=p;r.d=s;if(n<0){if(!g.lastIsInt){q.style.marginLeft=g.Em(n)}n=0}q.style.width=g.Em(n);q.style.height=g.Em(p+s);if(s){q.style.verticalAlign=g.Em(-s)}return q}});h.mpadded.Augment({toCommonHTML:function(u){u=this.CHTMLdefaultSpan(u,{childSpans:true,className:"MJXc-box",forceChild:true});var o=u.firstChild;var v=this.getValues("width","height","depth","lspace","voffset");var s=this.CHTMLdimen(v.lspace);var q=0,n=0,t=s.len,r=-s.len,p=0;if(v.width!==""){s=this.CHTMLdimen(v.width,"w",0);if(s.pm){r+=s.len}else{u.style.width=g.Em(s.len)}}if(v.height!==""){s=this.CHTMLdimen(v.height,"h",0);if(!s.pm){q+=-this.CHTMLbboxFor(0).h}q+=s.len}if(v.depth!==""){s=this.CHTMLdimen(v.depth,"d",0);if(!s.pm){n+=-this.CHTMLbboxFor(0).d;p+=-s.len}n+=s.len}if(v.voffset!==""){s=this.CHTMLdimen(v.voffset);q-=s.len;n+=s.len;p+=s.len}if(q){o.style.marginTop=g.Em(q)}if(n){o.style.marginBottom=g.Em(n)}if(t){o.style.marginLeft=g.Em(t)}if(r){o.style.marginRight=g.Em(r)}if(p){u.style.verticalAlign=g.Em(p)}return u},CHTMLdimen:function(q,r,n){if(n==null){n=-k}q=String(q);var o=q.match(/width|height|depth/);var p=(o?this.CHTML[o[0].charAt(0)]:(r?this.CHTML[r]:0));return{len:g.length2em(q,p)||0,pm:!!q.match(/^[-+]/)}}});h.munderover.Augment({toCommonHTML:function(q){var n=this.getValues("displaystyle","accent","accentunder","align");if(!n.displaystyle&&this.data[this.base]!=null&&this.data[this.base].CoreMO().Get("movablelimits")){q=h.msubsup.prototype.toCommonHTML.call(this,q);q.className=q.className.replace(/munderover/,"msubsup");return q}q=this.CHTMLdefaultSpan(q,{childSpans:true,className:"",noBBox:true});var p=this.CHTMLbboxFor(this.over),s=this.CHTMLbboxFor(this.under),u=this.CHTMLbboxFor(this.base),o=this.CHTML,r=p.acc;if(this.data[this.over]){q.lastChild.firstChild.style.marginLeft=p.l=q.lastChild.firstChild.style.marginRight=p.r=0;var t=e.Element("span",{},[["span",{className:"MJXc-over"}]]);t.firstChild.appendChild(q.lastChild);if(q.childNodes.length>(this.data[this.under]?1:0)){t.firstChild.appendChild(q.firstChild)}this.data[this.over].CHTMLhandleScriptlevel(t.firstChild.firstChild);if(r!=null){if(p.vec){t.firstChild.firstChild.firstChild.style.fontSize="60%";p.h*=0.6;p.d*=0.6;p.w*=0.6}r=r-p.d+0.1;if(u.t!=null){r+=u.t-u.h}t.firstChild.firstChild.style.marginBottom=g.Em(r)}if(q.firstChild){q.insertBefore(t,q.firstChild)}else{q.appendChild(t)}}if(this.data[this.under]){q.lastChild.firstChild.style.marginLeft=s.l=q.lastChild.firstChild.marginRight=s.r=0;this.data[this.under].CHTMLhandleScriptlevel(q.lastChild)}o.w=Math.max(0.8*p.w,0.8*s.w,u.w);o.h=0.8*(p.h+p.d+(r||0))+u.h;o.d=u.d+0.8*(s.h+s.d);return q}});h.msubsup.Augment({toCommonHTML:function(q){q=this.CHTMLdefaultSpan(q,{noBBox:true});if(!this.data[this.base]){if(q.firstChild){q.insertBefore(e.Element("span"),q.firstChild)}else{q.appendChild(e.Element("span"))}}var s=this.data[this.base],p=this.data[this.sub],n=this.data[this.sup];if(!s){s={bbox:{h:0.8,d:0.2}}}q.firstChild.style.marginRight=".05em";var o=Math.max(0.4,s.CHTML.h-0.4),u=Math.max(0.2,s.CHTML.d+0.1);var t=this.CHTML;if(n&&p){var r=e.Element("span",{className:"MJXc-script-box",style:{height:g.Em(o+n.CHTML.h*0.8+u+p.CHTML.d*0.8),"vertical-align":g.Em(-u-p.CHTML.d*0.8)}},[["span",{},[["span",{},[["span",{style:{"margin-bottom":g.Em(-(n.CHTML.d-0.05))}}]]]]],["span",{},[["span",{},[["span",{style:{"margin-top":g.Em(-(n.CHTML.h-0.05))}}]]]]]]);p.CHTMLhandleScriptlevel(r.firstChild);n.CHTMLhandleScriptlevel(r.lastChild);r.firstChild.firstChild.firstChild.appendChild(q.lastChild);r.lastChild.firstChild.firstChild.appendChild(q.lastChild);q.appendChild(r);t.h=Math.max(s.CHTML.h,n.CHTML.h*0.8+o);t.d=Math.max(s.CHTML.d,p.CHTML.d*0.8+u);t.w=s.CHTML.w+Math.max(n.CHTML.w,p.CHTML.w)+0.07}else{if(n){q.lastChild.style.verticalAlign=g.Em(o);n.CHTMLhandleScriptlevel(q.lastChild);t.h=Math.max(s.CHTML.h,n.CHTML.h*0.8+o);t.d=Math.max(s.CHTML.d,n.CHTML.d*0.8-o);t.w=s.CHTML.w+n.CHTML.w+0.07}else{if(p){q.lastChild.style.verticalAlign=g.Em(-u);p.CHTMLhandleScriptlevel(q.lastChild);t.h=Math.max(s.CHTML.h,p.CHTML.h*0.8-u);t.d=Math.max(s.CHTML.d,p.CHTML.d*0.8+u);t.w=s.CHTML.w+p.CHTML.w+0.07}}}return q}});h.mfrac.Augment({toCommonHTML:function(r){r=this.CHTMLdefaultSpan(r,{childSpans:true,className:"MJXc-box",forceChild:true,noBBox:true});var o=this.getValues("linethickness","displaystyle");if(!o.displaystyle){if(this.data[0]){this.data[0].CHTMLhandleScriptlevel(r.firstChild)}if(this.data[1]){this.data[1].CHTMLhandleScriptlevel(r.lastChild)}}var n=e.Element("span",{className:"MJXc-box",style:{"margin-top":"-.8em"}},[["span",{className:"MJXc-denom"},[["span",{},[["span",{className:"MJXc-rule"}]]],["span"]]]]);n.firstChild.lastChild.appendChild(r.lastChild);r.appendChild(n);var s=this.CHTMLbboxFor(0),p=this.CHTMLbboxFor(1),v=this.CHTML;v.w=Math.max(s.w,p.w)*0.8;v.h=s.h+s.d+0.1+0.25;v.d=p.h+p.d-0.25;v.l=v.r=0.125;o.linethickness=Math.max(0,g.length2em(o.linethickness||"0",0));if(o.linethickness){var u=n.firstChild.firstChild.firstChild;var q=g.Em(o.linethickness);u.style.borderTop=(o.linethickness<0.15?"1px":q)+" solid";u.style.margin=q+" 0";q=o.linethickness;n.style.marginTop=g.Em(3*q-0.9);r.style.verticalAlign=g.Em(1.5*q+0.1);v.h+=1.5*q-0.1;v.d+=1.5*q}return r}});h.msqrt.Augment({toCommonHTML:function(n){n=this.CHTMLdefaultSpan(n,{childSpans:true,className:"MJXc-box",forceChild:true,noBBox:true});this.CHTMLlayoutRoot(n,n.firstChild);return n},CHTMLlayoutRoot:function(u,n){var v=this.CHTMLbboxFor(0);var q=Math.ceil((v.h+v.d+0.14)*100),w=g.Em(14/q);var r=e.Element("span",{className:"MJXc-surd"},[["span",{style:{"font-size":q+"%","margin-top":w}},["\u221A"]]]);var s=e.Element("span",{className:"MJXc-root"},[["span",{className:"MJXc-rule",style:{"border-top":".08em solid"}}]]);var p=(1.2/2.2)*q/100;if(q>150){var o=Math.ceil(150/q*10);r.firstChild.className="MJXc-right MJXc-scale"+o;r.firstChild.style.marginLeft=g.Em(p*(o/10-1)/q*100);p=p*o/10;s.firstChild.style.borderTopWidth=g.Em(0.08/Math.sqrt(o/10))}s.appendChild(n);u.appendChild(r);u.appendChild(s);this.CHTML.h=v.h+0.18;this.CHTML.d=v.d;this.CHTML.w=v.w+p;return u}});h.mroot.Augment({toCommonHTML:function(q){q=this.CHTMLdefaultSpan(q,{childSpans:true,className:"MJXc-box",forceChild:true,noBBox:true});var p=this.CHTMLbboxFor(1),n=q.removeChild(q.lastChild);var t=this.CHTMLlayoutRoot(e.Element("span"),q.firstChild);n.className="MJXc-script";var u=parseInt(t.firstChild.firstChild.style.fontSize);var o=0.55*(u/120)+p.d*0.8,s=-0.6*(u/120);if(u>150){s*=0.95*Math.ceil(150/u*10)/10}n.style.marginRight=g.Em(s);n.style.verticalAlign=g.Em(o);if(-s>p.w*0.8){n.style.marginLeft=g.Em(-s-p.w*0.8)}q.appendChild(n);q.appendChild(t);this.CHTML.w+=Math.max(0,p.w*0.8+s);this.CHTML.h=Math.max(this.CHTML.h,p.h*0.8+o);return q},CHTMLlayoutRoot:h.msqrt.prototype.CHTMLlayoutRoot});h.mfenced.Augment({toCommonHTML:function(q){q=this.CHTMLcreateSpan(q);this.CHTMLhandleStyle(q);this.CHTMLhandleColor(q);this.addFakeNodes();this.CHTMLaddChild(q,"open",{});for(var p=0,n=this.data.length;p<n;p++){this.CHTMLaddChild(q,"sep"+p,{});this.CHTMLaddChild(q,p,{})}this.CHTMLaddChild(q,"close",{});var o=this.CHTML.h,r=this.CHTML.d;this.CHTMLstretchChild("open",o,r);for(p=0,n=this.data.length;p<n;p++){this.CHTMLstretchChild("sep"+p,o,r);this.CHTMLstretchChild(p,o,r)}this.CHTMLstretchChild("close",o,r);return q}});h.mrow.Augment({toCommonHTML:function(q){q=this.CHTMLdefaultSpan(q);var p=this.CHTML.h,r=this.CHTML.d;for(var o=0,n=this.data.length;o<n;o++){this.CHTMLstretchChild(o,p,r)}return q}});h.mstyle.Augment({toCommonHTML:function(n){n=this.CHTMLdefaultSpan(n);this.CHTMLhandleScriptlevel(n);return n}});h.TeXAtom.Augment({toCommonHTML:function(n){n=this.CHTMLdefaultSpan(n);n.className="MJXc-mrow";return n}});h.mtable.Augment({toCommonHTML:function(E){E=this.CHTMLdefaultSpan(E,{noBBox:true});var r=this.getValues("columnalign","rowalign","columnspacing","rowspacing","columnwidth","equalcolumns","equalrows","columnlines","rowlines","frame","framespacing","align","width");var u=MathJax.Hub.SplitList,F,A,D,z;var N=u(r.columnspacing),w=u(r.rowspacing),L=u(r.columnalign),t=u(r.rowalign);for(F=0,A=N.length;F<A;F++){N[F]=g.length2em(N[F])}for(F=0,A=w.length;F<A;F++){w[F]=g.length2em(w[F])}var K=e.Element("span");while(E.firstChild){K.appendChild(E.firstChild)}E.appendChild(K);var y=0,s=0;for(F=0,A=this.data.length;F<A;F++){var v=this.data[F];if(v){var J=g.arrayEntry(w,F-1),C=g.arrayEntry(t,F);var x=v.CHTML,q=v.CHTMLspanElement();q.style.verticalAlign=C;var B=(v.type==="mlabeledtr"?1:0);for(D=0,z=v.data.length;D<z-B;D++){var p=v.data[D+B];if(p){var M=g.arrayEntry(N,D-1),G=g.arrayEntry(L,D);var I=p.CHTMLspanElement();if(D){x.w+=M;I.style.paddingLeft=g.Em(M)}if(F){I.style.paddingTop=g.Em(J)}I.style.textAlign=G}}y+=x.h+x.d;if(F){y+=J}if(x.w>s){s=x.w}}}var o=this.CHTML;o.w=s;o.h=y/2+0.25;o.d=y/2-0.25;o.l=o.r=0.125;return E}});h.mlabeledtr.Augment({CHTMLdefaultSpan:function(q,o){if(!o){o={}}q=this.CHTMLcreateSpan(q);this.CHTMLhandleStyle(q);this.CHTMLhandleColor(q);if(this.isToken){this.CHTMLhandleToken(q)}for(var p=1,n=this.data.length;p<n;p++){this.CHTMLaddChild(q,p,o)}return q}});h.semantics.Augment({toCommonHTML:function(n){n=this.CHTMLcreateSpan(n);if(this.data[0]){this.data[0].toCommonHTML(n);MathJax.Hub.Insert(this.data[0].CHTML||{},this.CHTML)}return n}});h.annotation.Augment({toCommonHTML:function(n){}});h["annotation-xml"].Augment({toCommonHTML:function(n){}});MathJax.Hub.Register.StartupHook("onLoad",function(){setTimeout(MathJax.Callback(["loadComplete",g,"jax.js"]),0)})});MathJax.Hub.Register.StartupHook("End Cookie",function(){if(b.config.menuSettings.zoom!=="None"){i.Require("[MathJax]/extensions/MathZoom.js")}})})(MathJax.Ajax,MathJax.Hub,MathJax.HTML,MathJax.OutputJax.CommonHTML);
