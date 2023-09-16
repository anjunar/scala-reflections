package com.anjunar.reflections
package core.java.members

import com.anjunar.reflections.core.api.annotations.ResolvedAnnotation
import com.anjunar.reflections.core.api.members
import com.anjunar.reflections.core.api.members.ResolvedMember
import com.anjunar.reflections.core.api.nodes.ResolvedNode
import com.anjunar.reflections.core.api.types.ResolvedClass
import com.anjunar.reflections.core.java.nodes.JavaNode
import tastyquery.Contexts

import java.lang.reflect.{Member, Modifier}

abstract class JavaMember(underlying : Member, owner : ResolvedNode)(using context: Contexts.Context) extends JavaNode(underlying, owner) with ResolvedMember {
  
  override def compareTo(o: ResolvedMember): Int = ???
  
  override val sourcePosition: Int = -1
  
  override val modifier: members.ResolvedMember.Modifier =
    if Modifier.isPublic(underlying.getModifiers) then ResolvedMember.Modifier.Public else
      if Modifier.isPrivate(underlying.getModifiers) then ResolvedMember.Modifier.Private else
        if Modifier.isProtected(underlying.getModifiers) then ResolvedMember.Modifier.Protected else
          ResolvedMember.Modifier.Protected

}
