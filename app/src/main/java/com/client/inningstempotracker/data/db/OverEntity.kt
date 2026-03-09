package com.client.inningstempotracker.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "overs",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["matchId"])]
)
data class OverEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val matchId: Int,
    val overNumber: Int,
    val runs: Int,
    val wicket: Boolean,
    val phaseType: String,
    val note: String? = null
)