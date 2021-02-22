package stellaris.archeology

import arc.Core
import arc.func.Prov
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.layout.Table
import arc.struct.EnumSet
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.meta.BlockFlag
import stellaris.Main

open class ArcheologyBlock(name: String?) : Block(name) {
    inner class ArcheologyBuild : Building() {
        var lastEvent : ArcheologyEvent? = null
        var totalEvent: ArcheologyEvent? = null

        override fun updateTile() {
            val arch = Main.archeology
            val data: ArcheologyData = arch.getData(this.team)
            data.build = this
            if (data.beginEvent == null) return
            if (data.crafting && !data.finish) {
                data.progress += Time.delta.let { if(Main.test) it * 25 else it }
                if (data.progress >= 5f * Time.toMinutes) {
                    var event = arch.nextEvent(team)
                    Main.archeology.fireEvent(event, this)
                    data.schedule += event.schedule
                    data.crafting = false
                    data.progress = 0f
                    data.events += 1
                    configure(event)
                }
            }

            if ((data.events >= Archeology.max || data.schedule >= data.beginEvent.difficulty) && !data.finish) {
                if (data.schedule >= data.beginEvent.difficulty) {
                    arch.fireFinalEvent(this.team, this)
                }
                data.finish = true
            }
        }

        override fun add() {
            super.add()
            val event = Main.archeology.newBeginEvent(this)
            lastEvent = event
            totalEvent = event
        }

        override fun buildConfiguration(table: Table) {
            var consume = true
            val arch = Main.archeology!!
            val data: ArcheologyData = arch.getData(Vars.player.team())
            val ent: Building? = data.build

            table.table {
                it.add(totalEvent?.info())
            }
            table.row()
            table.table { t: Table ->
                data.totalEvents.each {
                    t.button(TextureRegionDrawable(Core.atlas.find(Main.transform(it.region)))) {
                        configure(it)
                    }.size(64f,64f)
                }
            }

            table.row()

            lastEvent?.requirements?.forEach {
                if (consume && !data.finish)
                    consume = ent?.items!!.has(it.item, it.amount)
            }

            val buttonName = if(data.schedule < data.beginEvent.difficulty && data.finish)
                "holy shit" else totalEvent?.buttonName ?: Core.bundle.get("continue")
            table.button(buttonName) {
                lastEvent?.requirements?.forEach {
                    ent?.items!!.remove(it.item, it.amount)
                }
                data.crafting = true
            }.disabled(!consume || data.finish || data.crafting).size(12f * buttonName.length, 25f)
        }

        override fun config(): Any? = totalEvent

        override fun write(write: Writes) {
            super.write(write)
            var arch = Main.archeology
            with(lastEvent!!.type!!.ordinal) {
                write.i(this)
                write.i(arch.events[this].indexOf(lastEvent))
            }
        }

        override fun read(read: Reads) {
            super.read(read)
            var arch = Main.archeology
            lastEvent = arch.events[read.i()].get(read.i())
        }
    }



    init {
        update = true
        solid = true
        hasItems = true
        sync = true
        flags = EnumSet.of(BlockFlag.factory)
        configurable = true
        saveConfig = true
        buildType = Prov { ArcheologyBuild() }
        config(ArcheologyEvent::class.java) { ent: Building, event: ArcheologyEvent ->
            ent as ArcheologyBuild
            val data = Main.archeology.getData(ent.team)
            if (data.totalEvents.contains(event))
                ent.totalEvent = event else data.totalEvents.add(event)
            ent.lastEvent = event
            Main.archeology.fireEvent(event, ent)
        }
        configClear { tile: Building? ->
            tile as ArcheologyBuild
            tile.totalEvent = null
        }
    }

    override fun setBars() {
        super.setBars()
        bars.add("time") { entity: Building ->
            Bar("time", Pal.sap) {
                Main.archeology.getData(entity.team).progress / (5f * Time.toMinutes)
            }
        }
    }
}