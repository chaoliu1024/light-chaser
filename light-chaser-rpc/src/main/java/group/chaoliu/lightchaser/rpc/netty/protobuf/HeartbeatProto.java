// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Heartbeat.proto

package group.chaoliu.lightchaser.rpc.netty.protobuf;

public final class HeartbeatProto {
  private HeartbeatProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface HeartbeatOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required int32 code = 1;
    /**
     * <code>required int32 code = 1;</code>
     */
    boolean hasCode();
    /**
     * <code>required int32 code = 1;</code>
     */
    int getCode();

    // required int64 heapMemory = 2;
    /**
     * <code>required int64 heapMemory = 2;</code>
     */
    boolean hasHeapMemory();
    /**
     * <code>required int64 heapMemory = 2;</code>
     */
    long getHeapMemory();

    // required int32 port = 3;
    /**
     * <code>required int32 port = 3;</code>
     */
    boolean hasPort();
    /**
     * <code>required int32 port = 3;</code>
     */
    int getPort();
  }
  /**
   * Protobuf type {@code protobuf.Heartbeat}
   */
  public static final class Heartbeat extends
      com.google.protobuf.GeneratedMessage
      implements HeartbeatOrBuilder {
    // Use Heartbeat.newBuilder() to construct.
    private Heartbeat(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private Heartbeat(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final Heartbeat defaultInstance;
    public static Heartbeat getDefaultInstance() {
      return defaultInstance;
    }

    public Heartbeat getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private Heartbeat(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              code_ = input.readInt32();
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              heapMemory_ = input.readInt64();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              port_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return HeartbeatProto.internal_static_protobuf_Heartbeat_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return HeartbeatProto.internal_static_protobuf_Heartbeat_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              Heartbeat.class, Builder.class);
    }

    public static com.google.protobuf.Parser<Heartbeat> PARSER =
        new com.google.protobuf.AbstractParser<Heartbeat>() {
      public Heartbeat parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Heartbeat(input, extensionRegistry);
      }
    };

    @Override
    public com.google.protobuf.Parser<Heartbeat> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required int32 code = 1;
    public static final int CODE_FIELD_NUMBER = 1;
    private int code_;
    /**
     * <code>required int32 code = 1;</code>
     */
    public boolean hasCode() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 code = 1;</code>
     */
    public int getCode() {
      return code_;
    }

    // required int64 heapMemory = 2;
    public static final int HEAPMEMORY_FIELD_NUMBER = 2;
    private long heapMemory_;
    /**
     * <code>required int64 heapMemory = 2;</code>
     */
    public boolean hasHeapMemory() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int64 heapMemory = 2;</code>
     */
    public long getHeapMemory() {
      return heapMemory_;
    }

    // required int32 port = 3;
    public static final int PORT_FIELD_NUMBER = 3;
    private int port_;
    /**
     * <code>required int32 port = 3;</code>
     */
    public boolean hasPort() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required int32 port = 3;</code>
     */
    public int getPort() {
      return port_;
    }

    private void initFields() {
      code_ = 0;
      heapMemory_ = 0L;
      port_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasCode()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasHeapMemory()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasPort()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, code_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt64(2, heapMemory_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt32(3, port_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, code_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, heapMemory_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, port_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    protected Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static Heartbeat parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Heartbeat parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Heartbeat parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static Heartbeat parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static Heartbeat parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static Heartbeat parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static Heartbeat parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static Heartbeat parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static Heartbeat parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static Heartbeat parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(Heartbeat prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code protobuf.Heartbeat}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements HeartbeatOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return HeartbeatProto.internal_static_protobuf_Heartbeat_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return HeartbeatProto.internal_static_protobuf_Heartbeat_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                Heartbeat.class, Builder.class);
      }

      // Construct using group.chaoliu.lightchaser.rpc.netty.protobuf.HeartbeatProto.Heartbeat.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        code_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        heapMemory_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        port_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return HeartbeatProto.internal_static_protobuf_Heartbeat_descriptor;
      }

      public Heartbeat getDefaultInstanceForType() {
        return Heartbeat.getDefaultInstance();
      }

      public Heartbeat build() {
        Heartbeat result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public Heartbeat buildPartial() {
        Heartbeat result = new Heartbeat(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.code_ = code_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.heapMemory_ = heapMemory_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.port_ = port_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof Heartbeat) {
          return mergeFrom((Heartbeat)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(Heartbeat other) {
        if (other == Heartbeat.getDefaultInstance()) return this;
        if (other.hasCode()) {
          setCode(other.getCode());
        }
        if (other.hasHeapMemory()) {
          setHeapMemory(other.getHeapMemory());
        }
        if (other.hasPort()) {
          setPort(other.getPort());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasCode()) {
          
          return false;
        }
        if (!hasHeapMemory()) {
          
          return false;
        }
        if (!hasPort()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        Heartbeat parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (Heartbeat) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required int32 code = 1;
      private int code_ ;
      /**
       * <code>required int32 code = 1;</code>
       */
      public boolean hasCode() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required int32 code = 1;</code>
       */
      public int getCode() {
        return code_;
      }
      /**
       * <code>required int32 code = 1;</code>
       */
      public Builder setCode(int value) {
        bitField0_ |= 0x00000001;
        code_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 code = 1;</code>
       */
      public Builder clearCode() {
        bitField0_ = (bitField0_ & ~0x00000001);
        code_ = 0;
        onChanged();
        return this;
      }

      // required int64 heapMemory = 2;
      private long heapMemory_ ;
      /**
       * <code>required int64 heapMemory = 2;</code>
       */
      public boolean hasHeapMemory() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int64 heapMemory = 2;</code>
       */
      public long getHeapMemory() {
        return heapMemory_;
      }
      /**
       * <code>required int64 heapMemory = 2;</code>
       */
      public Builder setHeapMemory(long value) {
        bitField0_ |= 0x00000002;
        heapMemory_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 heapMemory = 2;</code>
       */
      public Builder clearHeapMemory() {
        bitField0_ = (bitField0_ & ~0x00000002);
        heapMemory_ = 0L;
        onChanged();
        return this;
      }

      // required int32 port = 3;
      private int port_ ;
      /**
       * <code>required int32 port = 3;</code>
       */
      public boolean hasPort() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required int32 port = 3;</code>
       */
      public int getPort() {
        return port_;
      }
      /**
       * <code>required int32 port = 3;</code>
       */
      public Builder setPort(int value) {
        bitField0_ |= 0x00000004;
        port_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int32 port = 3;</code>
       */
      public Builder clearPort() {
        bitField0_ = (bitField0_ & ~0x00000004);
        port_ = 0;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:protobuf.Heartbeat)
    }

    static {
      defaultInstance = new Heartbeat(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:protobuf.Heartbeat)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_protobuf_Heartbeat_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_protobuf_Heartbeat_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\017Heartbeat.proto\022\010protobuf\";\n\tHeartbeat" +
      "\022\014\n\004code\030\001 \002(\005\022\022\n\nheapMemory\030\002 \002(\003\022\014\n\004po" +
      "rt\030\003 \002(\005B>\n,group.chaoliu.lightchaser.rp" +
      "c.netty.protobufB\016HeartbeatProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_protobuf_Heartbeat_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_protobuf_Heartbeat_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_protobuf_Heartbeat_descriptor,
              new String[] { "Code", "HeapMemory", "Port", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
