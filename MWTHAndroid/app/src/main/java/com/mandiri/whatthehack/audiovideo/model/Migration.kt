package com.mandiri.whatthehack.audiovideo.model

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import io.realm.RealmSchema

/**
 * Created by andreyyoshuamanik on 9/17/17.
 */

class Migration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion

        // DynamicRealm exposes an editable schema
        val schema = realm.schema

        if (oldVersion == 0L) {
            schema.get("User")
                .addField("name", String::class.java)
            oldVersion++
        }

        if (oldVersion == 1L) {

            schema.get("User")
                .addField("token", String::class.java)
            oldVersion++
        }

        if (oldVersion == 2L) {
            schema.create("Token")
                .addField("token", String::class.java)

            schema.get("User")
                .removeField("token")
            oldVersion++
        }


        if (oldVersion == 3L) {
            schema.get("User")
                .addField("myself", Boolean::class.javaPrimitiveType)
            oldVersion++
        }

        if (oldVersion == 4L) {
            schema.get("Chat")
                .addField("arrived", Boolean::class.javaPrimitiveType)
            oldVersion++
        }

        if (oldVersion == 5L) {
            schema.create("Group")
                .addField("_id", String::class.java)
                .addField("subject", String::class.java)
                .addRealmListField("users", schema.get("User"))
            oldVersion++
        }

        if (oldVersion == 6L) {
            schema.get("Group")
                .addField("createdDate", String::class.java)
                .addField("joinedDate", String::class.java)
            oldVersion++
        }

        if (oldVersion == 7L) {
            schema.get("ChatContent")
                .addField("type", String::class.java)
            oldVersion++
        }

        if (oldVersion == 8L) {
            schema.get("Chat")
                .removeField("delivered")
                .removeField("read")
                .removeField("arrived")
                .addField("deliveredTime", String::class.java)
                .addField("readTime", String::class.java)
                .addField("arrivedTime", String::class.java)
            oldVersion++
        }


        if (oldVersion == 9L) {
            schema.get("Chat")
                .addField("type", String::class.java)
            oldVersion++
        }

        if (oldVersion == 10L) {
            schema.create("GroupChatDeliveredTime")
                .addField("userId", String::class.java)
                .addField("deliveredTime", String::class.java)

            schema.create("GroupChatReadTime")
                .addField("userId", String::class.java)
                .addField("readTime", String::class.java)

            schema.get("Chat")
                .addRealmListField("groupChatDeliveredTimes", schema.get("GroupChatDeliveredTime"))
                .addRealmListField("groupChatReadTimes", schema.get("GroupChatReadTime"))

            oldVersion++
        }

        if (oldVersion == 11L) {
            schema.create("StarredChat")
                .addField("chatId", String::class.java)
            oldVersion++
        }

        if (oldVersion == 12L) {
            schema.get("StarredChat")
                .addPrimaryKey("chatId")
            oldVersion++
        }

        if (oldVersion == 13L) {
            schema.get("User")
                .addField("status", String::class.java)
                .addField("profilePictUrl", String::class.java)
            oldVersion++
        }

    }
}
