/**
 *  ESP Easy DTH (v.0.0.1)
 *
 * MIT License
 *
 * Copyright (c) 2018 
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
 
import groovy.json.JsonSlurper
import groovy.transform.Field


metadata {
	definition (name: "ESP sprinkler", namespace: "ShinJjang", author: "ShinJjang", vid: "generic-valve", ocfDeviceType: "oic.d.watervalve") {
		capability "Actuator"
		capability "Switch"
        capability "Sensor"
        capability "Refresh"
		capability "Valve"
        
        attribute "mode", "enum", ["mist", "rest", "off"]        
        
        attribute "lastCheckinDate", "date"
        
        command "setData"
        command "refresh"
        command "timerLoop"
		command	"checkNewDay"
        command "sprinkler"


	}


	simulator {
	}
    preferences {
		input "url", "text", title: "ESP IP주소", description: "로컬IP 주소를 입력", required: true
		input "gpio", "enum", title: "GPIO Pin", description: "릴레이 배당 GPIO Pin", defaultValue: 30, options:[9: "GPIO 9(D11)", 10 : "GPIO 10(D12)", 12: "GPIO 12(D6)", 13: "GPIO 13(D7)", 14: "GPIO 14(D5)", 15: "GPIO 15(D8)", 16: "GPIO 16(D0)"], displayDuringSetup: true
		input "OnTime", "enum", title: "미스트 작동시간", defaultValue: 30, options:[5: "5 sec", 10: "10 sec", 20: "20 sec", 30 : "30 sec", 60: "1 min", 120 :"2 min", 180 :"3 min", 300 :"5 min"], displayDuringSetup: true
		input "OffTime", "enum", title: "미스트 휴지시간", defaultValue: 30, options:[5: "5 sec", 10: "10 sec", 20: "20 sec", 30 : "30 sec", 60: "1 min", 120 :"2 min", 180 :"3 min", 300 :"5 min", 600: "10 min"], displayDuringSetup: true

    }

	tiles {
    
		multiAttributeTile(name:"mode", type: "generic", width: 6, height: 4){
			tileAttribute ("device.mode", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'${name}', action:"on", icon:"st.valves.water.closed", backgroundColor:"#ffffff", nextState:"mist"
                attributeState "mist", label:'${name}', action:"off", icon:"st.valves.water.open", backgroundColor:"#73C1EC", nextState:"off"
                attributeState "rest", label:'${name}', action:"off", icon:"st.valves.water.open", backgroundColor:"#6eca8f", nextState:"off"
			}
            
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    			attributeState("default", label:'${currentValue}',icon: "st.Health & Wellness.health7")
            }
		}
		multiAttributeTile(name:"valve", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.valve", key: "PRIMARY_CONTROL") {
				attributeState "open", label: '${name}', action: "valve.close", icon: "st.valves.water.open", backgroundColor: "#00A0DC", nextState:"closing"
				attributeState "closed", label: '${name}', action: "valve.open", icon: "st.valves.water.closed", backgroundColor: "#ffffff", nextState:"opening"
				attributeState "opening", label: '${name}', action: "valve.close", icon: "st.valves.water.open", backgroundColor: "#00A0DC"
				attributeState "closing", label: '${name}', action: "valve.open", icon: "st.valves.water.closed", backgroundColor: "#ffffff"
			}
		}    
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC"
		}
		main "valve"
		details "mode", "switch"
	}

}


def parse(String description) {
	log.debug "Parsing '${description}'"
}

def updated() {
    log.debug "URL >> ${url}"
	state.address = url
    state.gpiopin = gpio
    state.offTime = OffTime
    state.onTime = OnTime
    sendEvent(name: "lastCheckin", value: "Mist time:  " + state.onTime +" sec, Rest time:  " + state.offTime + " sec")
    }

def on(){
	sprayOn()    
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "valve", value: "open")
}

def off(){
	sprayOff()
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "valve", value: "closed")
    sendEvent(name: "mode", value: "off")
    unschedule()
}

def close(){
	sprayOff()
    sendEvent(name: "switch", value: "off")
    sendEvent(name: "valve", value: "closed")
    sendEvent(name: "mode", value: "off")
    unschedule()
}

def open(){
	sprayOn()    
    sendEvent(name: "switch", value: "on")
    sendEvent(name: "valve", value: "open")
}
def sprayOn() {
    try{
        def options = [
            "method": "GET",
            "path": "/control?cmd=gpio,${state.gpiopin},0",
            "headers": [
                "HOST": state.address + ":80",
                "Content-Type": "application/json"
            ]
        ]
        def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: callback])
        sendHubCommand(myhubAction)
    }catch(e){
    	log.error "Error!!! ${e}"
    }
	startTimerOff(state.onTime.toInteger(), sprayOff)
    sendEvent(name: "mode", value: "mist")
}

def sprayOff() {
    try{
        def options = [
            "method": "GET",
            "path": "/control?cmd=gpio,${state.gpiopin},1",
            "headers": [
                "HOST": state.address + ":80",
                "Content-Type": "application/json"
            ]
        ]
        def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: callback])
        sendHubCommand(myhubAction)
    }catch(e){
    	log.error "Error!!! ${e}"
    }
	startTimerOn(state.offTime.toInteger(), sprayOn)
    sendEvent(name: "mode", value: "rest")
}


def startTimerOff(seconds, function) {
	runIn(seconds, function) 
}

def startTimerOn(seconds, function) {
	runIn(seconds, function) 
} 
/*
def callback(physicalgraph.device.HubResponse hubResponse){
	def msg, json, status
    try {
        msg = parseLanMessage(hubResponse.description)
        log.debug msg.body
        def jsonObj = new JsonSlurper().parseText(msg.body)

//        def jsonObj = msg.json
        setData(jsonObj)
//	log.debug "SetData >> ${jsonObj.Sensors}"
        
    } catch (e) {
        log.error "Exception caught while parsing data: " + e 
    }
}

def refresh() {
	getStatusOfESPEasy()
}

def pollco() {
    def url = "https://api.thingspeak.com/update?key=${apiKey}&field${coField}=${state.carbonDioxide}"
    httpGet(url) { 
        response -> 
        if (response.status != 200 ) {
            log.debug "ThingSpeak logging failed, status = ${response.status}"
        }
    }
    def refreshTime = (updatecycle as int) * 60
    	runIn(refreshTime, pollco)        
        log.debug "Update Temperature to ThingSpeak = ${state.carbonDioxide}C"
}    

def getStatusOfESPEasy() {
    try{
//    	def timeGap = new Date().getTime() - Long.valueOf(state.lastTime)
//        if(timeGap > 1000 * 60){
//            log.warn "ESP Easy device is not connected..."
//        }
//		log.debug "Try to get data from ${state.address}"
        def options = [
            "method": "GET",
            "path": "/control?cmd=Servo,1,15,90",
            "headers": [
                "HOST": state.address + ":80",
                "Content-Type": "application/json"
            ]
        ]
        def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: callback])
        sendHubCommand(myhubAction)
    }catch(e){
    	log.error "Error!!! ${e}"
    }
}



try {
    httpPut("http://192.168.31.74/control?cmd=Servo,1,15,90") { resp ->
        log.debug "response data: ${resp.data}"
        log.debug "response contentType: ${resp.contentType}"
    }
} catch (e) {
    log.debug "something went wrong: $e"
}
    try{
		log.debug "Try to get data from ${state.address}"
        def options = [
            "method": "GET",
            "path": "/control?cmd=Servo,1,15,90",
            "headers": [
                "HOST": state.address + ":80",
                "Content-Type": "application/json"
            ]
        ]
        def myhubAction = new physicalgraph.device.HubAction(options, null, [callback: callback])
        sendHubCommand(myhubAction)
    }catch(e){
    	log.error "Error!!! ${e}"
    }
}*/