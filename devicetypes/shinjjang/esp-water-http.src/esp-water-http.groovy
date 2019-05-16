/**
 *  ESP Easy CO2 DTH (v.0.0.1)
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
	definition (name: "ESP WATER HTTP", namespace: "ShinJjang", author: "ShinJjang") {
        capability "Sensor"
		capability "Relative Humidity Measurement"
		capability "Battery"
        capability "Refresh"
        
		attribute "waterHeight", "number"
		attribute "waterVolume", "number"
		attribute "waterLevel", "number"
		attribute "w1_value", "number"
		attribute "w2_value", "number"
		attribute "w3_value", "number"
		attribute "w4_value", "number"
		attribute "w5_value", "number"
		attribute "w6_value", "number"
		attribute "distance", "number"
		attribute "maxLux", "number"
		attribute "minLux", "number"
		attribute "maxTemp", "number"
		attribute "minTemp", "number"
		attribute "maxCo", "number"
		attribute "minCo", "number"
	    attribute "lastCheckinDate", "date"
		attribute "carbonDioxideSet", "string"               
	    command "setData"
	    command "refresh"
        command "timerLoop"
        command "averageCo"
        command "updateMinMaxTemps"
        command "updateMinMaxCo"
		command	"checkNewDay"
        command "checkPPM"
        command "ledOnsec"

	}


	simulator {
	}
    preferences {
		input "url", "text", title: "ESP IP주소", description: "로컬IP 주소를 입력", required: true
        input name: "email", type: "email", title: "Email", description: "Enter Email Address", required: true, displayDuringSetup: true
        input "waterDepthMax", "number", title: "최저수위시 물탱크 깊이", description:"센서로 부터 최저수위까지의 거리(50cm~300cm)", range: "50..300", defaultValue: 100, required: true
        input "waterDepthMin", "number", title: "최대수위시 물탱크 깊이", description:"센서로 부터 최대수위까지의 거리(21cm~300cm)", range: "21..300", defaultValue: 21, required: true
        input "tankW", "number", title: "물탱크 가로", description:"물탱크 가로길이(cm)", range: "1..300", defaultValue: 120, required: true
        input "tankD", "number", title: "물탱크 세로", description:"물탱크 세로길이(cm)", range: "1..300", defaultValue: 120, required: true
    }

	tiles(scale: 2) {
    		multiAttributeTile(name:"main", type:"generic", width:6, height:4) {
			tileAttribute("device.waterLevel", key: "PRIMARY_CONTROL") {
            	attributeState "waterLevel", icon:"https://www.shareicon.net/data/128x128/2016/08/04/806522_refresh_512x512.png", label:'${currentValue}%', backgroundColors:[
 				[value: 20, color: "#d6e0ff"],
 				[value: 40, color: "#adc1ff"],
                [value: 60, color: "#84a3ff"],
                [value: 80, color: "#5b84ff"],
                [value: 100, color: "#3366ff"]
				]
            }
            tileAttribute ("device.lastCheckin", key: "SECONDARY_CONTROL") {
				attributeState "lastCheckin", label:'Updated: ${currentValue}'
			}
		} 
        valueTile("distance_label", "", decoration: "flat", width:2, height:1) {
            state "default", label:'표면까지거리'
        }
        valueTile("waterVolume_label", "", decoration: "flat", width:2, height:1) {
            state "default", label:'물양'
        }
        valueTile("waterHeight_label", "", decoration: "flat", width:2, height:1) {
            state "default", label:'물 높이'
        }
        valueTile("distance", "device.distance", width:2, height:2) {
            state "distance", label:'${currentValue}cm', backgroundColor: "#ffcc5c"
            }
        valueTile("waterVolume", "device.waterVolume", width:2, height:2) {
            state "waterVolume", label:'${currentValue}ℓ', backgroundColor: "#ffcc5c"
            }
        valueTile("waterHeight", "device.waterHeight", width:2, height:2) {
            state "waterHeight", label:'${currentValue}cm', backgroundColor: "#ffcc5c"
            }
        valueTile("w1_label", "", decoration: "flat", width:1, height:1) {
            state "default", label:'측정값1'
        }
        valueTile("w2_label", "", decoration: "flat", width:1, height:1) {
            state "default", label:'측정값2'
        }
        valueTile("w3_label", "", decoration: "flat", width:1, height:1) {
            state "default", label:'측정값3'
        }
        valueTile("w4_label", "", decoration: "flat", width:1, height:1) {
            state "default", label:'측정값4'
        }
        valueTile("w5_label", "", decoration: "flat", width:1, height:1) {
            state "default", label:'측정값5'
        }
        valueTile("w6_label", "", decoration: "flat", width:1, height:1) {
            state "default", label:'측정값6'
        }
        valueTile("w1_value", "device.w1_value", decoration: "flat", width:1, height:1) {
            state "default", label:'${currentValue}'
        }
        valueTile("w2_value", "device.w2_value", decoration: "flat", width:1, height:1) {
            state "default", label:'${currentValue}'
        }
        valueTile("w3_value", "device.w3_value", decoration: "flat", width:1, height:1) {
            state "default", label:'${currentValue}'
        }
        valueTile("w4_value", "device.w4_value", decoration: "flat", width:1, height:1) {
            state "default", label:'${currentValue}'
        }
        valueTile("w5_value", "device.w5_value", decoration: "flat", width:1, height:1) {
            state "default", label:'${currentValue}'
        }
        valueTile("w6_value", "device.w6_value", decoration: "flat", width:1, height:1) {
            state "default", label:'${currentValue}'
        }
        valueTile("humidity", "device.humidity", inactiveLabel: false, width: 2, height: 2) {
            state "humidity", label:'${currentValue}%', unit:"%"
            }
        valueTile("battery", "device.battery", inactiveLabel: false, width: 2, height: 2) {
            state "battery", label:'${currentValue}%', unit:"%"
            }

        valueTile("refresh_label", "", decoration: "flat", width:2, height:1) {
            state "default", label:'새로고침'
        }

       
       	main (["main"])
      	details(["main", "waterVolume_label", "waterHeight_label", "distance_label", 
        		"waterVolume", "waterHeight", "distance", 
                "w1_label", "w2_label", "w3_label", "w4_label", "w5_label", "w6_label", 
                "w1_value", "w2_value", "w3_value", "w4_value", "w5_value", "w6_value", 
                ])
	}
}


def updated() {
    log.debug "URL >> ${url}"
	state.address = url
    state.lastTime = new Date().getTime()
//    state.averageNumber = averageNumber as int
//    state.homekit = homekitSet
    averageReset()
}

def checkNewDay() {
	def now = new Date().format("yyyy-MM-dd", location.timeZone)
	if(state.prvDate == null){
		state.prvDate = now
	}else{
		if(state.prvDate != now){
			state.prvDate = now
			    log.debug "checkNewDay _ ${state.prvDate}"
			resetMinMax()            
		}
	}
}

def averageWater(distance) {
	def cm = distance
    log.debug "averageWater dis-cm ${cm}, ${state.w1}"
	if(state.w1 == 0) {
    	state.w1 = cm
    sendEvent(name: "w1_value", value: state.w1)
	} else if(state.w2 == 0) {
    	state.w2 = cm
        if( state.w1 >= state.w2) {
        state.maxD = state.w1
        state.minD = state.w2
        state.minD2 = state.w1
        } else {
        state.maxD = state.w2
        state.minD = state.w1
        state.minD2 = state.w2
        }
    log.debug "1 Max: ${state.maxD}, Min: ${state.minD}, Min2: ${state.minD2}"        
//        max1 = max(state.w1, state.w2) as int
//        min1 = min(state.w1, state.w2) as int
    sendEvent(name: "w2_value", value: state.w2)
	} else if(state.w3 == 0) {
    	state.w3 = cm
        if( state.w3 > state.maxD) {
        state.maxD = state.w3
        } 
        else if ( state.w3 < state.minD2) {
        	if( state.w3 < state.minD) {
        		state.minD = state.w3
        	} else {state.minD2 = state.w3
        	}
        }
    log.debug "2 Max: ${state.maxD}, Min: ${state.minD}, Min2: ${state.minD2}"        

    sendEvent(name: "w3_value", value: state.w3)
	} else if(state.w4 == 0) {
    	state.w4 = cm
        if( state.w4 > state.maxD) {
        state.maxD = state.w4
        } 
        else if ( state.w4 < state.minD2) {
        	if( state.w4 < state.minD) {
        		state.minD = state.w4
        	} else {state.minD2 = state.w4
        	}
        }
    log.debug "3 Max: ${state.maxD}, Min: ${state.minD}, Min2: ${state.minD2}"        
    sendEvent(name: "w4_value", value: state.w4)
	} else if(state.w5 == 0) {
    	state.w5 = cm
        if( state.w5 > state.maxD) {
        state.maxD = state.w5
        } 
        else if ( state.w5 < state.minD2) {
        	if( state.w5 < state.minD) {
        		state.minD = state.w5
        	} else {state.minD2 = state.w5
        	}
        }
    log.debug "4 Max: ${state.maxD}, Min: ${state.minD}, Min2: ${state.minD2}"        
    sendEvent(name: "w5_value", value: state.w5)
	} else if(state.w6 == 0) {
    	state.w6 = cm
        if( state.w6 > state.maxD) {
        state.maxD = state.w6
        } 
        else if ( state.w6 < state.minD2) {
        	if( state.w6 < state.minD) {
        		state.minD = state.w6
        	} else {state.minD2 = state.w6
        	}
        }
    log.debug "5 Max: ${state.maxD}, Min: ${state.minD}, Min2: ${state.minD2}"        
    sendEvent(name: "w6_value", value: state.w6)
			pollWaterAverage()
			averageReset()
	} else { 
    	log.debug "something wrong ${state.w1}, ${state.w2}, ${state.w3}, ${state.w4}, ${state.w5}, ${state.w6}"
		averageReset()
}        
}       

def pollWaterAverage() {
	log.debug "Wateraverage start( ${state.w1}, ${state.w2}, ${state.w3}, ${state.w4}, ${state.w5}, ${state.w6}, Max: ${state.maxD}, Min: ${state.minD}, Min2: ${state.minD2})"
//    maxDis = Math.max(32, 32, 35, 35, 31, 30)
//    maxDis = Math.max(state.w1, state.w2, state.w3, state.w4, state.w5, state.w6)
//    maxDis = Math.max(state.w1 as int, state.w2 as int, state.w3 as int, state.w4 as int, state.w5 as int, state.w6 as int)
//    log.debug "Max Water ${maxDis}"
	state.averageDis = (state.w1 + state.w2 + state.w3 + state.w4 + state.w5 + state.w6 - state.maxD - state.minD - state.minD2)/3
    log.debug "Wateraverage Dis result: ${state.averageDis}"
       sendEvent(name:"distance", value: state.maxD, unit: "cm" )
       state.wHeight = waterDepthMax - state.maxD
       state.amount = (waterDepthMax - state.maxD)/(waterDepthMax - waterDepthMin)*100
       state.wVolume = tankW * tankD * state.amount /1000
		log.debug "Average Distance: ${state.maxD} Amount: ${state.amount}"

/*       state.wHeight = waterDepthMax - state.averageDis
       state.amount = (waterDepthMax - state.averageDis)/(waterDepthMax - waterDepthMin)*100
       state.wVolume = tankW * tankD * state.amount /1000
		log.debug "Average Distance: ${state.averageDis} Amount: ${state.amount}"  */
       sendEvent(name:"waterLevel", value: state.amount )
       sendEvent(name:"waterVolume", value: state.wVolume as int)
       sendEvent(name:"waterHeight", value: state.wHeight as int, unit: "cm" )
       sendEvent(name:"battery", value: state.amount as int, unit: "%" )
       sendEvent(name:"humidity", value: state.amount as int, unit: "%" )

    
//    log.debug "CO2average = ${state.averageCo},( ${state.co1}, ${state.co2}, ${state.co3}, ${state.co4}, ${state.co5}, ${state.co6}, ${state.co7}, ${state.co8}, ${state.co9}, ${state.co10},/ ${state.averageNumber})"
//    pollco()
//    pollcoubi()
}

def averageReset() {
	log.debug "Wateraverage reset"
	state.w1 = 0
	state.w2 = 0
	state.w3 = 0
	state.w4 = 0
	state.w5 = 0
	state.w6 = 0
    state.maxD = 0
    state.minD = 0
}



def resetMinMax() {	
	def currentTemp = device.currentValue('temperature')
	def currentCo = device.currentValue('carbonDioxide')
    currentTemp = currentTemp ? (int) currentTemp : currentTemp
	log.debug "${device.displayName}: Resetting daily min/max values to current temperature of ${currentTemp}° and humidity of ${currentCo}%"
    sendEvent(name: "maxTemp", value: currentTemp)
    sendEvent(name: "minTemp", value: currentTemp)
    sendEvent(name: "maxCo", value: currentCo)
    sendEvent(name: "minCo", value: currentCo)
}


def updateMinMaxTemps(temp) {
	def ttime = new Date().format("a hh:mm", location.timeZone)
	if ((temp > device.currentValue('maxTemp')) || (device.currentValue('maxTemp') == null)){
		sendEvent(name: "maxTemp", value: temp)	
		sendEvent(name: "maxTempTime", value: ttime)	
	} else if ((temp < device.currentValue('minTemp')) || (device.currentValue('minTemp') == null)){
		sendEvent(name: "minTemp", value: temp)
		sendEvent(name: "minTempTime", value: ttime)
    }
}

def updateMinMaxCo(co) {
	def ttime = new Date().format("a hh:mm", location.timeZone)
    def hk = state.homekit as int
    if ( co >= hk ) {
    	sendEvent(name: "carbonDioxideSet", value: "high", displayed: false)
        log.debug "homekitarm 'high'"
    } else {
    	sendEvent(name: "carbonDioxideSet", value: "normal", displayed: false)
        log.debug "homekitarm 'normal'"
    }

	if ((co > device.currentValue('maxCo')) || (device.currentValue('maxCo') == null)){
		sendEvent(name: "maxCo", value: co)
		sendEvent(name: "maxCoTime", value: ttime)	
	} else if ((co < device.currentValue('minCo')) || (device.currentValue('minCo') == null)){
		sendEvent(name: "minCo", value: co)
		sendEvent(name: "minCoTime", value: ttime)
    }
}

def refresh() {
}

def parse(String description) {
    def events = []

    log.debug "Parsing '${description}'"
    def msg = parseLanMessage(description)
    log.debug "headers ${msg.headers}"
    log.debug "header ${msg.header}"
//    log.debug "json ${msg.json}"
//    log.debug "status ${msg.status}"
//    log.debug "data ${msg.data}"
//    log.debug "body ${msg.body}"

    
    def desc = msg.header.toString()
	log.debug "def: descr ${desc}"

	def descr = desc.split("&")[1]
	log.debug "def: descr1 ${descr}"


    def slurper = new JsonSlurper()
    def result = slurper.parseText(descr)
    log.debug "def: descr3 ${result}"
    
    if (result.containsKey("Distance")) {
       state.distance = result.Distance as int
       averageWater(state.distance)
    }

    if (result.containsKey("CO2")) {
       state.carbonDioxide = result.CO2 as int
       updateMinMaxCo(state.carbonDioxide)
       events << createEvent(name:"carbonDioxide", value: state.carbonDioxide, unit: "ppm" )
    }
    if (result.containsKey("U")) {
    }
    if (result.containsKey("Temperature")) {
	   	state.temperature = result.Temperature
     	updateMinMaxTemps(state.temperature)
       	events << createEvent(name:"temperature", value: state.temperature, unit: "C")
    }
    if (result.containsKey("Lux")) {
		state.light = result.Lux
		events << createEvent(name:"illuminance", value: state.light, unit: "lux")
    }
    if (result.containsKey("Infrared")) {
		events << createEvent(name:"infraredIndex", value:result.Infrared)
    }
    if (result.containsKey("Broadband")) {
		events << createEvent(name:"broadband", value:result.Broadband)
    }
    
    	def nowk = new Date().format("yyyy-MM-dd HH:mm:ss", location.timeZone)
        def now = new Date()
        state.lastTime = now.getTime()
        sendEvent(name: "lastCheckin", value: nowk)
        checkNewDay()

    return events    
}

def callback(physicalgraph.device.HubResponse hubResponse){
	def msg, json, status
    try {
//        msg = parseLanMessage(hubResponse.description)
//        log.debug msg.body
//        def jsonObj = new JsonSlurper().parseText(msg.body)

//        def jsonObj = msg.json
//        setData(jsonObj)
//	log.debug "SetData >> ${jsonObj.Sensors}"
        
    } catch (e) {
        log.debug "Done"
    }
}