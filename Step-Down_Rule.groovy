definition(
    name: "Step-Down Rule",
    namespace: "BD",
    author: "Bobby Dobrescu",
    description: "Dim a light before it turns off",
    parent: "BD:Step-down Rules",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: "")

preferences {
	page(name: "mainPage")
}

def mainPage() {
	dynamicPage(name: "mainPage", title: " ", install: true, uninstall: true) {
		section {
			input "appName", "text", title: "Name this rule", submitOnChange: true
			    if(appName) app.updateLabel("$appName")
			input "dSwitch", "capability.switchLevel", title: "Select Dimmer", submitOnChange: true, required: true, multiple: false
                if (dSwitch) input "dLevel", "number", range: "0..100", title: "Set step-down level before turning off", required: true, submitOnChange: true, description: "0..100"
            input "dMotion", "capability.motionSensor", title: "Select Motion Sensor", submitOnChange: true, required: true, multiple: false
                if (dMotion) input "minutes", "number", title: "Step-down when motion stops after this number of minutes", submitOnChange: true, description: "Enter number of minutes"
                if (minutes) input "enableOff", "bool", title: "Enable turning the light off?", required: false, defaultValue: false, submitOnChange: true
                if (enableOff) input "turnOff", "number", title: "Turn off after this number of minutes", submitOnChange: true, description: "Enter number of minutes"
		    input "logging", "bool", title: "Enable logging", width: 6
        }
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
    log.debug "initializing"
	subscribe(dMotion, "motion", handler)
}

def handler(evt) {
    //log.info "event received $evt"
    if(state.priorLevel != dSwitch.currentLevel && dSwitch.currentLevel > dLevel ) {
        state.priorLevel = dSwitch.currentLevel
        if (logging) log.info "saving current level $dSwitch.currentLevel"
    }
    else if (logging) log.info "skipping saving level because current level $dSwitch.currentLevel and saved level is $state.priorLevel"
  
    if(evt.value == "active" && dSwitch.currentSwitch.contains("on") ) {
        dSwitch.setLevel(state.priorLevel)
        unschedule()
        if (logging) log.info "restoring previous level and canceling timers"
    }
	
    if (evt.value == "inactive" && dSwitch.currentSwitch.contains("on")) {
        if(minutes > 0) {
            runIn(minutes * 60, stepDown)
            if (logging) log.info "setting timer for $minutes minutes"
        }    
        else {
            unschedule()
            if (logging) log.info "canceling timers"
        }
    }
}
    
def stepDown () {
    
    if (dSwitch.currentSwitch.contains("on")){
        dSwitch.setLevel(dLevel)
        log.info "setting step-down light to $dLevel level"
        if (enableOff){
            runIn(turnOff * 60, turnOffAction)
            if (logging) log.info "scheduling timer to turn light off in $turnOff minutes"
        }
    }
    else log.info "not taking action because the light appears to be off"
}
    
def turnOffAction () {
    if (logging) log.info "turning off light"
    dSwitch.off()  
}
        
