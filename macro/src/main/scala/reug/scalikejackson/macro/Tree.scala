package reug.scalikejackson.`macro`

import java.util

import scala.collection.JavaConverters._

private[`macro`] object Root {
    val ROOT_FIELD_NAME = "_ROOT_"

    def apply(): Tree = Node(ROOT_FIELD_NAME, new util.LinkedList[Tree]())
}

private[`macro`] abstract class Tree(val key: String) {
    self =>
    def append[T](keys: Seq[String], v: T): Tree = {
        def append(node: Tree, keys: Seq[String], v: T): Tree = {
            keys match {
                case h :: Nil =>
                    node match {
                        case Node(_, ch) =>
                            ch.add(Leaf(h, v))
                            self
                        case _ =>
                            throw new Exception("append recursion non Node error")
                    }
                case h :: tail =>
                    node match {
                        case Node(_, ch) =>
                            ch.asScala.find(_.key == h).fold({
                                val n = Node(h, new util.LinkedList[Tree]())
                                ch.add(n)
                                append(n, tail, v)
                            }) {
                                case n@Node(_, _) =>
                                    append(n, tail, v)
                                case l@Leaf(_, _) =>
                                    val n = Node(h, new util.LinkedList[Tree]())
                                    ch.remove(l)
                                    n.children.add(l)
                                    ch.add(n)
                                    append(n, tail, v)
                            }
                        case _ =>
                            throw new Exception("append recursion non Node error")
                    }
            }
        }

        append(self, keys, v)
    }

    def traverse(root: Tree): String = {
        root match {
            case Node(k, c) =>
                c.asScala.foldLeft(startObjectWithKey(k))((a, e) => a + traverse(e)) + endObject
            case Leaf(k, (t: String, v: String)) =>
                if (isOptional(t))
                    s"""if (${prepareValue(v)}.isDefined)
                       |$generatorName.${writerByType(t)}("$k", ${prepareValue(v)}.get)\n""".stripMargin
                else
                    s"""$generatorName.${writerByType(t)}("$k", ${prepareValue(v)})\n"""
        }
    }
}

private[`macro`] case class Node(override val key: String, children: util.LinkedList[Tree]) extends Tree(key)

private[`macro`] case class Leaf[T](override val key: String, tpl: T) extends Tree(key)
