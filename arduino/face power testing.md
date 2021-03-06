I went ahead and did some testing on the face to get an idea of how much power it is going to require.

From testing I have seen about 300-550 mA being drawn depending on the animations and other factors.

For the purposes of this calculation I am going to round this up to 700 mA.

I am also going to assume that our circuit to step up the 3.7V average of the battery to 5V (for USB) is about 80% efficient. I will use this same value when calculating the efficiency of charging the battery.

With the 5000 mAh single cell battery we currently have in our possession, I estimate **4.22 hours of battery life.**

To charge this "beast" using the 1.0 A 5V charger we have, using the above stats, I estimate it to take about **15 hours to charge with the face on**


*Note:* ~~we also have a 1.5 A 5V charger which I have yet to test and verify it works as advertised. BUT if it does work, that gives us a much better charge time: **5.78 hours to charge with the face on**~~ 
This other charger is actually rated for 500mA, less than what I thought. We are stuck with using the 1.0 A charger. I ordered another set of these for backup.

---

I just want to throw an idea out there: if we are able to tell if the battery is charging with the arduino, we could turn off some of the panels to reduce the charge time OR prolong battery life by only having some on when not plugged in and charged.

---

I haven't done the calculation with the speaker yet (much harder to measure) but I imagine it being less power hungry. Also it was rated less important than the face by DHS.

---

Putting chargers in parallel with some fancy wiring on the batteries and output? Theoretically this might work, but as we know this is also theoretically possible for diodes. However in reality it is actually a terrible idea and doesn't work (one diode does all the work and the others end up acting like heat-sinks). I suspect this would be the case for these chargers as well.

**Important Assumptions**:

 - This is a conservative estimate, almost worst case. In fact I know for sure the battery will last longer and charge faster than stated above. So basically, this isn't an *Apple iPhone Battery life listing, if you know what I mean*

 - The battery charger is a "dumb" charger, ie it doesn't have a timeout and will continually charge the battery until it is full.

 - The battery charger may have issues supplying more than 500mA when the battery votlage drops below 3.5V, I will have to test this.

*Written By:* Colin Gagich
